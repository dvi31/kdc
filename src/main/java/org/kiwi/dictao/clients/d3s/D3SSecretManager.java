package org.kiwi.dictao.clients.d3s;

import com.dictao.d3s.wsdl.v2.authority.EnvironmentFaultException;
import com.dictao.d3s.wsdl.v2.authority.UserFaultException;
import com.dictao.d3s.wsdl.v2_1.authority.GrantDeleteResponse;
import com.dictao.d3s.wsdl.v2_1.authority.GrantReadResponse;
import com.dictao.d3s.wsdl.v2_1.authority.GrantWriteResponse;
import com.dictao.d3s.xsd.v2010_10.common.AccessPermissions;
import com.dictao.d3s.xsd.v2010_10.common.Metadata;
import com.dictao.d3s.xsd.v2010_10.common.MetadataValueType;
import com.dictao.d3s.xsd.v2010_10.common.Metadatas;
import com.dictao.d3s.xsd.v2010_10.common.Value;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.kiwi.dictao.clients.d3s.D3SConstants.D3SError;
import org.kiwi.utils.Logger;

/**
 * Classe d'appel au D3S
 *
 * @author i2165aq
 *
 */
public class D3SSecretManager {

    private final Logger log = Logger.getInstance();
    private final AuthorityProxy authProxy;
    private final StorageProxy stoProxy;

    /**
     *
     * @param authProxy
     * @param stoProxy
     */
    public D3SSecretManager(AuthorityProxy authProxy, StorageProxy stoProxy) {
        this.authProxy = authProxy;
        this.stoProxy = stoProxy;
    }

    /**
     *
     * @param userName
     * @param motivation
     * @param depositPath
     * @return
     */
    public String read(String userName, String motivation, String depositPath) {
        try {
            String pwdData = null;

            if ((depositPath == null) || (userName == null)) {
                return pwdData;
            }

            //Get read credentials
            GrantReadResponse grantReadResponse;

            grantReadResponse = authProxy.getSecretPort().grantRead(userName,
                    motivation, depositPath);

            if (grantReadResponse != null) {
                com.dictao.d3s.wsdl.v2_1.storage.ReadResponse readResponse
                        = stoProxy.getSecretPort().read(
                                grantReadResponse.getSecurityToken(),
                                grantReadResponse.getDepositProof());

                if (readResponse != null) {
                    byte[] dataRead = readResponse.getData();
                    pwdData = "";
                    for (int i = 0; i < dataRead.length; i++) {
                        pwdData += String.valueOf((char) dataRead[i]);
                    }

                }
            }

            return pwdData;
        } catch (EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        } catch (UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        } catch (com.dictao.d3s.wsdl.v2.storage.EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        } catch (com.dictao.d3s.wsdl.v2.storage.UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param userName
     * @param motivation
     * @param containerPath
     * @param metadata
     * @param data
     * @return
     */
    public int write(String userName, String motivation, String containerPath, Metadatas metadata, String data) {
        try {
            if ((userName == null) || (metadata == null)
                    || (containerPath == null) || (data == null)) {
                throw new IllegalArgumentException("Parameters cannot be null: "
                        + "(userName, depositName, containerPath, pwdData");
            }

            //Get write credentials
            GrantWriteResponse grantWriteResponse = authProxy.getSecretPort().grantWrite(userName,
                    motivation, containerPath);

            //metadatas autorisant la suppression
            if (grantWriteResponse != null) {
                String writeResponse = stoProxy.getSecretPort().write(userName,
                        motivation,
                        containerPath,
                        grantWriteResponse.getSecurityToken(),
                        grantWriteResponse.getCertificates(),
                        metadata,
                        data.getBytes());
            }

            return D3SError.NO_ERROR.value;
        } catch (EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_GRANT_WRITE.value + D3SError.ERROR_WRITE.value;
        } catch (UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_GRANT_WRITE.value + D3SError.ERROR_WRITE.value;
        } catch (com.dictao.d3s.wsdl.v2.storage.EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_WRITE.value;
        } catch (com.dictao.d3s.wsdl.v2.storage.UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_WRITE.value;
        }
    }

    /**
     *
     * @param userName
     * @param motivation
     * @param resourcePath
     * @return
     */
    public int setDeletable(String userName, String motivation, String resourcePath) {
        try {
            if (userName == null || resourcePath == null) {
                throw new IllegalArgumentException("Parameters cannot be null: "
                        + "(userName, resourcePath");
            }

            AccessPermissions accessPermissions = new AccessPermissions();
            accessPermissions.setDeletable(true);

            authProxy.getDepositPort().changeAccessPermissions(userName,
                    motivation, resourcePath, accessPermissions);
            return D3SError.NO_ERROR.value;
        } catch (EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        } catch (UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        }
        return D3SError.ERROR_SETPERMISSION.value;
    }

    /**
     *
     * @param userName
     * @param motivation
     * @param resourcePath
     * @return
     */
    public int delete(String userName, String motivation, String resourcePath) {
        if (userName == null || resourcePath == null) {
            throw new IllegalArgumentException("Parameters cannot be null: "
                    + "(userName, resourcePath");
        }

        try {
            GrantDeleteResponse grantDeleteResponse = authProxy.getSecretPort().grantDelete(
                    userName, motivation, resourcePath);
            if (grantDeleteResponse != null) {
                stoProxy.getSecretPort().delete(userName, motivation,
                        resourcePath, grantDeleteResponse.getSecurityToken());
                return D3SError.NO_ERROR.value;
            }
        } catch (EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_GRANT_DELETE.value + D3SError.ERROR_DELETE.value;
        } catch (UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_GRANT_DELETE.value + D3SError.ERROR_DELETE.value;
        } catch (com.dictao.d3s.wsdl.v2.storage.EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_DELETE.value;
        } catch (com.dictao.d3s.wsdl.v2.storage.UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
            return D3SError.ERROR_DELETE.value;
        }

        return D3SError.ERROR_DELETE.value;
    }

    /**
     *
     * @param userName
     * @param motivation
     * @param resourcePath
     * @return
     */
    public int discard(String userName, String motivation, String resourcePath) {
        try {

            authProxy.getDepositPort().discardDeposit(userName, motivation, resourcePath);

            return D3SError.NO_ERROR.value;

        } catch (EnvironmentFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        } catch (UserFaultException e) {
            log.println(this.getClass().getName() + " " + e.getMessage());
        }
        return D3SError.ERROR_DISCARD.value;
    }

    /**
     *
     * @param login
     * @param domain
     * @return
     */
    public Metadatas buildPasswordMetadata(String login, String domain) {
        Metadatas metadataList = new Metadatas();

        buildMetadata(metadataList, "appLogin", login);
        buildMetadata(metadataList, "appDomainName", domain);
        return metadataList;
    }

    /**
     *
     * @param metadataList
     * @param name
     * @param value
     */
    private void buildMetadata(Metadatas metadataList, String name, String value) {
        Metadata md = new Metadata();

        md.setName(name);
        md.setType(MetadataValueType.STRING_TYPE);
        Value val = new Value();
        val.setStringValue(value);
        md.setValue(val);
        metadataList.getMetadata().add(md);
    }

}
