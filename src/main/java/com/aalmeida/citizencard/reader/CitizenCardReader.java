/*
 * Copyright (c) 2018 Alexandre Almeida.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.aalmeida.citizencard.reader;

import com.aalmeida.citizencard.reader.model.CardData;
import com.aalmeida.citizencard.reader.model.ReadingStatus;
import com.aalmeida.citizencard.logging.Loggable;
import com.aalmeida.citizencard.reader.model.EventData;
import com.aalmeida.citizencard.reader.model.ReaderRef;
import org.slf4j.LoggerFactory;
import pt.gov.cartaodecidadao.PTEID_EIDCard;
import pt.gov.cartaodecidadao.PTEID_EId;
import pt.gov.cartaodecidadao.PTEID_Exception;
import pt.gov.cartaodecidadao.PTEID_ReaderContext;
import pt.gov.cartaodecidadao.PTEID_ReaderSet;
import pt.gov.cartaodecidadao.PTEID_ulwrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CitizenCardReader implements Loggable {

    private static final HashMap<String, ReaderRef> READER_REFERENCE_HANDLER = new HashMap<>();

    private static final String PIC_PATH = "pictures";

    private static boolean libLoaded;

    private final List<CitizenCardEventListener> listeners = new ArrayList<>();

    public CitizenCardReader() { }

    public void init() {
        try {
            for (int i = 0; i < PTEID_ReaderSet.instance().readerCount(); i++) {
                final PTEID_ReaderContext readerContext = PTEID_ReaderSet.instance().getReaderByNum(i);

                String readerName = readerContext.getName();
                final EventData eventData = new EventData(readerName);

                long handle = readerContext.SetEventCallback((l, statusCode, data) -> {
                    try {
                        if (!readerContext.isCardPresent()) {
                            if (statusCode == 18) {
                                sendCardChangedEvent(ReadingStatus.NO_CARD);
                            } else if (statusCode == 546) {
                                sendCardChangedEvent(ReadingStatus.NOT_CC_CARD);
                            }
                            logger().trace("Card not present on reader '{}'. statusCode={}", readerName, statusCode);
                            return;
                        }

                        ReaderRef nh = READER_REFERENCE_HANDLER.get(readerName);
                        PTEID_ulwrapper wrapCardID = new PTEID_ulwrapper(nh.getCardID());

                        if (readerContext.isCardChanged(wrapCardID)) {
                            logger().trace("Card present on reader '{}'. statusCode={}", readerName, statusCode);
                            sendCardChangedEvent(ReadingStatus.READING);

                            nh.setCardID(wrapCardID.m_long);

                            logger().trace("Card changed on card reader '{}'. cardId={}", readerName, wrapCardID.m_long);
                            CardData citizenCard = read(readerContext.getEIDCard());

                            if (statusCode == 34) {
                                sendCardChangedEvent(ReadingStatus.READ);
                                sendCardReadEvent(citizenCard);
                            }
                        }
                    } catch (Exception e) {
                        logger().error("Error while processing event callback.", e);
                        sendCardChangedEvent(ReadingStatus.ERROR);
                    }
                }, eventData);

                ReaderRef nh = new ReaderRef(readerName, handle);
                READER_REFERENCE_HANDLER.put(readerName, nh);
            }

        } catch (PTEID_Exception e) {
            logger().error("Failed to init citizen card", e);
        }
    }

    public void release() {
        try {
            PTEID_ReaderSet.releaseSDK();
        } catch (PTEID_Exception e) {
            logger().error("Failed to release citizen card", e);
        }
    }

    private CardData read(PTEID_EIDCard idCard) {
        CardData ccData = null;
        try {
            PTEID_EId cardData = idCard.getID();

            ccData = new CardData.Builder()
                    .setGivenName(cardData.getGivenName())
                    .setSurname(cardData.getSurname())
                    .setGender(cardData.getGender())
                    .setDateOfBirth(cardData.getDateOfBirth())

                    .setCardNumber(cardData.getDocumentNumber())
                    .setCivilianIdNumber(cardData.getCivilianIdNumber())
                    .setTaxNumber(cardData.getTaxNo())
                    .setSocialSecurityNumber(cardData.getSocialSecurityNumber())
                    .setHealthNumbe(cardData.getHealthNumber())

                    .setAccidentalIndications(cardData.getAccidentalIndications())

                    .setValidityBeginDate(cardData.getValidityBeginDate())
                    .setValidityEndDate(cardData.getValidityEndDate())
                    .build();

            logger().debug("Card readed. cardData={}", ccData);
        } catch (PTEID_Exception e) {
            logger().error("Failed to read card", e);
        }
        return ccData;
    }

    public ReadingStatus getStatus() {
        try {
            for (String readerName : READER_REFERENCE_HANDLER.keySet()) {
                final PTEID_ReaderContext readerContext = PTEID_ReaderSet.instance().getReaderByName(readerName);

                if (!readerContext.isCardPresent()) {
                    return ReadingStatus.NO_CARD;
                }

                ReaderRef nh = READER_REFERENCE_HANDLER.get(readerName);
                PTEID_ulwrapper wrapCardID = new PTEID_ulwrapper(nh.getCardID());

                if (readerContext.isCardChanged(wrapCardID)) {
                    return ReadingStatus.READING;
                }

                return ReadingStatus.READ;
            }
        } catch (Exception e) {
            logger().error("Error while processing event callback.", e);
            return ReadingStatus.ERROR;
        }

        return ReadingStatus.NO_CARD;
    }

    public CardData getCardData() {
        try {
            for (String readerName : READER_REFERENCE_HANDLER.keySet()) {
                final PTEID_ReaderContext readerContext = PTEID_ReaderSet.instance().getReaderByName(readerName);

                if (!readerContext.isCardPresent()) {
                    return null;
                }

                ReaderRef nh = READER_REFERENCE_HANDLER.get(readerName);
                PTEID_ulwrapper wrapCardID = new PTEID_ulwrapper(nh.getCardID());

                if (readerContext.isCardChanged(wrapCardID)) {
                    return read(readerContext.getEIDCard());
                }

                return read(readerContext.getEIDCard());
            }
        } catch (Exception e) {
            logger().error("Error while processing event callback.", e);
        }

        return null;
    }

    /**
     * Adds the listener.
     *
     * @param toAdd
     *            the to add
     */
    public void addListener(CitizenCardEventListener toAdd) {
        listeners.add(toAdd);
    }

    private void sendCardChangedEvent(ReadingStatus status) {
        for (CitizenCardEventListener listener : listeners) {
            listener.cardChangedEvent(status);
        }
    }

    private void sendCardReadEvent(CardData citizenCardData) {
        for (CitizenCardEventListener listener : listeners) {
            listener.cardReadEvent(citizenCardData);
        }
    }

    private void savePhoto(final byte[] picture, final String photoName) throws IOException {
        InputStream in = new ByteArrayInputStream(picture);
        BufferedImage img = ImageIO.read(in);

        File f = new File(PIC_PATH, photoName + "." + "bmp");
        if (!f.exists()) {
            f.mkdirs();
            f.createNewFile();
        }
        ImageIO.write(img, "bmp", f);
        System.out.println(f);
        ImageIO.read(f);
    }

    public static boolean loadLibrary() throws UnsatisfiedLinkError {
        return ((Boolean) AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                LoggerFactory.getLogger(CitizenCardReader.class).info("Load libraries from configured path '{}'",
                        System.getProperty("java.library.path"));
                String osName = System.getProperty("os.name");
                if (osName.equals("Mac OS X")) {
                    System.load("/usr/local/lib/libpteidlibj.2.dylib");
                } else if (osName.equals("Linux")) {
                    System.load("/usr/local/lib/libpteidlibj.so");
                } else {
                    System.loadLibrary("pteidlibj");
                }
                libLoaded = true;
            } catch (UnsatisfiedLinkError e) {
                if (!e.getMessage().contains("already loaded")) {
                    LoggerFactory.getLogger(CitizenCardReader.class).error("Cannot load library, check if you had installed Autenticação.gov application.", e);
                }
                libLoaded = false;
            }
            return libLoaded;
        }));
    }
}

