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

import com.aalmeida.citizencard.controller.entities.CitizenCardData;
import com.aalmeida.citizencard.controller.entities.ReadingStatus;
import com.aalmeida.citizencard.logging.Loggable;
import com.aalmeida.citizencard.reader.model.EventData;
import org.slf4j.LoggerFactory;
import pt.gov.cartaodecidadao.Callback;
import pt.gov.cartaodecidadao.PTEID_EIDCard;
import pt.gov.cartaodecidadao.PTEID_Exception;
import pt.gov.cartaodecidadao.PTEID_ID;
import pt.gov.cartaodecidadao.PTEID_PIC;
import pt.gov.cartaodecidadao.PTEID_ReaderContext;
import pt.gov.cartaodecidadao.PTEID_ReaderSet;
import pt.gov.cartaodecidadao.PTEID_TokenInfo;
import pt.gov.cartaodecidadao.PteidException;
import pt.gov.cartaodecidadao.pteid;
import pt.gov.cartaodecidadao.pteidlibJava_WrapperConstants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class CitizenCard.
 *
 * @author Alexandre
 */
public class CitizenCard implements Loggable {

    private static boolean libLoaded;

    private PTEID_ReaderSet readerSet = null;
    private PTEID_ReaderContext readerContext = null;
    private PTEID_EIDCard idCard = null;

    private static final String PIC_PATH = "pictures";
    private List<CitizenCardEventListener> listeners = new ArrayList<>();
    private CitizenCardData ccData;
    private ReadingStatus.Status ccStatus;

    public CitizenCard() {
        ccData = null;
        ccStatus = null;
    }

    public void init() {
        try {
            PTEID_ReaderSet.initSDK();
            readerSet = PTEID_ReaderSet.instance();
            Object x = new Object();
            readerSet.getReader().SetEventCallback(new Callback() {
                @Override
                public void getEvent(long l, long statusCode, Object data) {
                    logger().info("Event code {} {}", statusCode, l);
                    if (statusCode == pteidlibJava_WrapperConstants.EIDMW_OK) {
                        read();
                    }
                }
            }, x);
        } catch (PTEID_Exception ex) {
            ex.printStackTrace();
        }
    }

    private void release() {
        try {
            PTEID_ReaderSet.releaseSDK();
        } catch (PTEID_Exception ex) {
            ex.printStackTrace();
        }
    }

    public void read() {
        try {
            idCard = readerContext.getEIDCard();

            ccData = new CitizenCardData();
            ccData.setFirstName(idCard.getID().getGivenName());
            ccData.setSurname(idCard.getID().getSurname());
            ccData.setNif(idCard.getID().getTaxNo());

        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

    }

    /*public boolean checkCard() {
        if (!libLoaded) {
            ccStatus = ReadingStatus.Status.ERROR;
            return false;
        }
        try {
            pteid.Init("");
            pteid.SetSODChecking(false);

            PTEID_TokenInfo tokenInfo = pteid.GetTokenInfo();
            final String token = tokenInfo.serial;
            if (ccData != null && ccData.getToken().equals(token)) {
                return false;
            }
            if (token != null) {
                ccStatus = ReadingStatus.Status.READING;
                //sendNotification(null, ccStatus);
            }
            ccData = new CitizenCardData();
            ccData.setToken(token);
            while (ccData.getFirstName() == null) {
                PTEID_ID idData = pteid.GetID();
                ccData.setFirstName(idData.firstname);
                ccData.setSurname(idData.name);
                ccData.setNif(idData.numNIF);
                ccStatus = ReadingStatus.Status.READ;
            }
            ccStatus = ReadingStatus.Status.READING;
            //sendNotification(ccData, ccStatus);

            PTEID_PIC picData = pteid.GetPic();
            if (null != picData) {
                try {
                    savePhoto(picData.picture, ccData.getNif());
                } catch (FileNotFoundException e) {
                    logger().error("File not found exception. ", e);
                } catch (Exception e) {
                    logger().error("Fail to save photo. ", e);
                }
            }
            ccStatus = ReadingStatus.Status.READ;

            if (logger().isDebugEnabled()) {
                logger().debug("Citizen card data: {}", ccData);
            }

        } catch (PTEID_Exception e) {
            ccData = null;
            logger().trace("Card not present. errorCode={}", e.GetError());

            ccStatus = ReadingStatus.Status.ERROR;
            for (ReadingStatus.Status status : ReadingStatus.Status.values()) {
                if (status.getErrorCode() == e.GetError()) {
                    ccStatus = status;
                }
            }
            //sendNotification(null, ccStatus);

            return false;
        } finally {
            try {
                pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD);
            } catch (PteidException e) {
                logger().error("Fail to unload card. ", e);
            }
        }

        return true;
    }*/

    /**
     * Adds the listener.
     *
     * @param toAdd
     *            the to add
     */
    public void addListener(CitizenCardEventListener toAdd) {
        listeners.add(toAdd);
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public CitizenCardData getData() {
        return ccData;
    }

    public ReadingStatus.Status getStatus() {
        return ccStatus;
    }

    private void sendNotification(final CitizenCardData data, final  ReadingStatus.Status status) {
        if (listeners != null) {
            for (CitizenCardEventListener listener : listeners) {
                listener.cardChangedEvent(data, status);
            }
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
                LoggerFactory.getLogger(CitizenCard.class).info("Load libraries from configured path '{}'",
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
                    LoggerFactory.getLogger(CitizenCard.class).error("Cannot load library, check if you had installed Autenticação.gov application.", e);
                }
                libLoaded = false;
            }
            return libLoaded;
        }));
    }
}

