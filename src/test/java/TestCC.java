import java.io.*;

import javax.security.cert.CertificateException;

import pteidlib.PTEID_ADDR;
import pteidlib.PTEID_Certif;
import pteidlib.PTEID_ID;
import pteidlib.PTEID_PIC;
import pteidlib.PTEID_TokenInfo;
import pteidlib.PteidException;
import pteidlib.pteid;

public class TestCC {
    static {
        try {
            System.loadLibrary("pteidlibj");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("N�o foi possivel carregar a biblioteca.\n" + e);
            System.exit(1);
        }
    }

    public void PrintIDData(PTEID_ID idData) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nNome : " + idData.firstname);
        sb.append("\nApelido: " + idData.name);

        sb.append("\n\nData de Nascimento: " + idData.birthDate);
        sb.append("\nAltura: " + idData.height);
        sb.append("\nSexo: " + idData.sex);

        sb.append("\n\nNome do pai: " + idData.firstnameFather);
        sb.append("\nApelido do pai: " + idData.nameFather);
        sb.append("\nNome da m�e: " + idData.firstnameMother);
        sb.append("\nApelido da m�e: " + idData.nameMother);

        sb.append("\n\nN�mero de Id. Civil: " + idData.numBI);
        sb.append("\nN�mero de Id. Fiscal: " + idData.numNIF);
        sb.append("\nN�mero do Servi�o Nacional de Sa�de: " + idData.numSNS);
        sb.append("\nN�mero da Seguran�a Social: " + idData.numSS);

        sb.append("\n\nN�mero do cart�o: " + idData.cardNumber);
        sb.append("\nPAN (primary account number) do Cart�o: " + idData.cardNumberPAN);
        sb.append("\nVers�o do Cart�o: " + idData.cardVersion);

        sb.append("\nNacionalidade: " + idData.nationality);
        sb.append("\nPa�s: " + idData.country);

        sb.append("\nTipo de documento: " + idData.documentType);
        sb.append("\nData de entrega: " + idData.deliveryDate);
        sb.append("\nEntidade da Entrega: " + idData.deliveryEntity);
        sb.append("\nLocal: " + idData.locale);
        sb.append("\nMRZ 1: " + idData.mrz1);
        sb.append("\nMRZ 2: " + idData.mrz2);
        sb.append("\nMRZ 3: " + idData.mrz3);
        sb.append("\nNotas: " + idData.notes);

        System.out.println("Dados do cart�o:\n" + sb.toString());
    }

    public void PrintAddressData(PTEID_ADDR adData) {
        if (adData.addrType == "N") {
            System.out.println("Tipo de morada : National");
            System.out.println("Rua : " + adData.street);
            System.out.println("Concelho : " + adData.municipality);
            System.out.println("...");
        } else {
            System.out.println("Tipo de morada : International");
            System.out.println("Endere�o : " + adData.addressF);
            System.out.println("Cidade : " + adData.cityF);
            System.out.println("Rua : " + adData.street);
            System.out.println("Numero : " + adData.numMor);
            System.out.println("...");
        }
    }

    public static void main(String[] args) {
        TestCC test = new TestCC();
        try {
            pteid.Init("");

            pteid.SetSODChecking(false);
            // Don't check the integrity of the ID, address and photo (!)
            // pteid.SetSODChecking(true);

            // Leitura de propriedades do "token" - Numero de serie e
            // identificador do cart�o - ECF#1
            PTEID_TokenInfo token = pteid.GetTokenInfo();
            System.out.println("Token: " + token.label + "\nN. Serie: " + token.serial);

            // Leitura dos certificados digitais do cartao - ECF#3
            PTEID_Certif[] certs = pteid.GetCertificates();
            System.out.println("Encontrados " + certs.length + " certificados");
            for (int i = 0; i < certs.length; i++) {
                try {
                    javax.security.cert.X509Certificate x509 = javax.security.cert.X509Certificate
                            .getInstance(certs[i].certif);
                    System.out.println("\nCertificado " + i + ":" + "\nDN do Certificado: " + x509.getSubjectDN()
                            + "\nDN do Emissor" + x509.getIssuerDN() + "\nValido at�: " + x509.getNotAfter());
                } catch (CertificateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            // Leitura de dados de identificacao do cidadao - ECF#5
            PTEID_ID idData = pteid.GetID();
            if (null != idData) {
                System.out.println("Dados de identifica��o:");
                test.PrintIDData(idData);
            }

            // Leitura de dados da zona privada do cartao - ECF#7
            byte[] filein = { 0x3F, 0x00, 0x5F, 0x00, (byte) 0xEF, 0x07 };
            byte[] data2;

            data2 = pteid.ReadFile(filein, (byte) 0x81);
            System.out.println("Dados da zona privada: " + new String(data2));

            // Escrita de dados na zona privada do cartao - ECF#9
            String data = "Teste de escrita na zona privada do cart�o de cidad�o";
            pteid.WriteFile(filein, data.getBytes(), (byte) 0x81);
            System.out.println("Dados escritos no cart�o!");

            // Leitura da morada do cidadao - ECF#11
            PTEID_ADDR adData = pteid.GetAddr();
            if (null != adData) {
                System.out.println("Morada:");
                test.PrintAddressData(adData);
            }

            // Leitura da fotografia do cidadao - ECF#13
            PTEID_PIC picData = pteid.GetPic();
            if (null != picData) {
                try {
                    String photo = "photo.jp2";
                    FileOutputStream oFile = new FileOutputStream(photo);
                    oFile.write(picData.picture);
                    oFile.close();
                    System.out.println("Created " + photo);
                } catch (FileNotFoundException excep) {
                    System.out.println(excep.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Leitura do SOD do cartao - ECF#15
            System.out.println("SOD: " + new String(pteid.ReadSOD()));

            pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD);

        } catch (PteidException ex) {
            System.out.println(ex.getMessage());
            int errorNumber = Integer.parseInt(ex.getMessage().split("Error code : -")[1]);
            switch (errorNumber) {
            case 1101:
                System.out
                        .println("\nErro desconhecido - ??? Problemas com o servi�o de leitor de cart�es ???\nMessage: ");
                break;
            case 1104:
                System.out
                        .println("\n\nN�o foi poss�vel aceder ao cart�o.\nVerifique se est� correctamente inserido no leitor");
                break;
            case 1109:
                System.out.println("\n\nAc��o cancelada pelo utilizador.\n[Cancel na assinatura]");
                break;
            case 1210:
                System.out.println("\n\nO cart�o inserido n�o corresponde a um cart�o de cidad�o v�lido.");
                break;
            default:
                System.out.println(ex.getMessage());
                break;
            }
            ex.printStackTrace();
        }
    }
}
