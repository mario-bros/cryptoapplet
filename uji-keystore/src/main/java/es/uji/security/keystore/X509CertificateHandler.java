package es.uji.security.keystore;

import java.security.Provider;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateParsingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import es.uji.security.crypto.SupportedKeystore;
import es.uji.security.keystore.IKeyStore;

/**
 * _keyUsage is a boolean array where positions are:
 * 
 * digitalSignature a[0]; nonRepudiation a[1]; keyEncipherment a[2]; dataEncipherment a[3];
 * keyAgreement a[4]; keycertSign a[5]; CRLSign a[6]; encipherOnly a[7]; decipherOnly a[8];
 */

public class X509CertificateHandler
{
    private String _SubjectDN;
    private String _SubjectCN;
    private String _IssuerDN;
    private String _IssuerOrganization;
    private String _alias;
    private SupportedKeystore _storeName;
    private String _tokenName;
    private Provider _provider;
    private List<String> _extKeyUsage;
    private X509Certificate _xcer = null;
    private IKeyStore _iksh = null;

    boolean[] _keyUsage;
    boolean _emailProtection = false;

    private String[] _keyUsageStr = { "digitalSignature", "nonRepudiation", "keyEncipherment",
            "dataEncipherment", "keyAgreement", "keycertSign", "CRLSign", "encipherOnly",
            "decipherOnly", };

    public X509CertificateHandler(X509Certificate xcer, String alias, IKeyStore iksh)
            throws CertificateParsingException
    {
        _iksh = iksh;
        initialize(xcer, alias, _iksh.getProvider(), _iksh.getName(), _iksh.getTokenName());
    }

    public X509CertificateHandler(X509Certificate xcer, String alias, Provider provider,
            SupportedKeystore storeName, String tokenName) throws CertificateParsingException
    {
        _iksh = null;
        initialize(xcer, alias, provider, storeName, tokenName);
    }

    public void initialize(X509Certificate xcer, String alias, Provider provider, SupportedKeystore storeName,
            String tokenName) throws CertificateParsingException
    {
        _SubjectDN = xcer.getSubjectDN().getName();
        _IssuerDN = xcer.getIssuerDN().getName();
        _keyUsage = xcer.getKeyUsage();
        _extKeyUsage = xcer.getExtendedKeyUsage();
        _alias = alias;
        _provider = provider;
        _storeName = storeName;
        _xcer = xcer;
        _tokenName = tokenName;

        extractIssuerOrganizationFromDN();
        extractSubjectFromDN();
    }

    private void extractSubjectFromDN()
    {
        String auxStr = _SubjectDN.substring(_SubjectDN.indexOf("CN="));
        auxStr = auxStr.replaceFirst("CN=", "");

        if (auxStr.indexOf("=") > -1)
        {
            auxStr = auxStr.substring(0, auxStr.indexOf("=") - 3);
        }

        auxStr = auxStr.replace('\"', ' ');
        auxStr = auxStr.trim();

        auxStr = auxStr.trim();
        if (auxStr.charAt(auxStr.length() - 1) == ',')
        {
            auxStr = auxStr.substring(0, auxStr.length() - 2);
        }

        _SubjectCN = auxStr;

        if (_SubjectCN == null ||_SubjectCN.isEmpty())
        {
            _SubjectCN = extractDNField("OU", _SubjectDN);
        }
    }

    private void extractIssuerOrganizationFromDN()
    {
        String auxStr = "Unknown";
        int auxIdx = _IssuerDN.indexOf("O=");

        if (auxIdx != -1)
        {
            auxStr = _IssuerDN.substring(auxIdx);
            auxStr = auxStr.replaceFirst("O=", "");
        }
        else
        {
            auxIdx = _IssuerDN.indexOf("O =");

            if (auxIdx != -1)
            {
                auxStr = _IssuerDN.substring(auxIdx);
                auxStr = auxStr.replaceFirst("O =", "");
            }
        }

        if (auxStr.indexOf("=") > -1)
        {
            auxStr = auxStr.substring(0, auxStr.indexOf(","));
        }

        auxStr = auxStr.replace('\"', ' ');
        auxStr = auxStr.trim();

        _IssuerOrganization = auxStr;
    }

    private String join(java.util.Collection collection, String separator) {
        if (collection == null) return "";

        Iterator iterator = collection.iterator();

        if (iterator == null)
        {
            return null;
        }

        if (!iterator.hasNext())
        {
            return "";
        }

        Object first = iterator.next();

        if (!iterator.hasNext())
        {
            return (first == null) ? "" : first.toString();
        }

        StringBuffer buf = new StringBuffer(256);

        if (first != null)
        {
            buf.append(first);
        }

        while (iterator.hasNext())
        {
            if (separator != null)
            {
                buf.append(separator);
            }

            Object obj = iterator.next();

            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }

    private String extractDNField(String fieldToExtract, String dn)
    {
        if (fieldToExtract == null || dn == null) return "";

        List<String> values = new ArrayList<String>();

        for (String field : dn.split(","))
        {
            String fieldType = field.trim().split("=")[0];
            String fieldValue = field.trim().split("=")[1];

            if (fieldToExtract.equalsIgnoreCase(fieldType))
            {
                values.add(fieldValue);
            }
        }

        return join(values, " - ");
    }

    public X509Certificate getCertificate()
    {
        return _xcer;
    }

    public String getAlias()
    {
        try
        {
            if (_iksh != null)
                return _iksh.getAliasFromCertificate(_xcer);
        }
        catch (Exception e)
        {

        }
        return null;
    }

    public String getIssuerOrganization()
    {
        return _IssuerOrganization;
    }

    public String getExtendedInfo()
    {
        String auxStr = "\n  Emitido por:     " + _IssuerOrganization + "\n";
        auxStr += "  Pertenece a:     " + _SubjectCN + "\n";
        auxStr += "  Uso de la llave: ";

        // System.out.println("SubjCN: " +auxStr);

        if (_keyUsage != null)
        {

            for (int i = 0; i < _keyUsage.length; i++)
            {
                if (_keyUsage[i])
                {
                    auxStr += _keyUsageStr[i] + ", ";
                }
            }
        }

        if (_extKeyUsage != null)
        {
            for (String u : _extKeyUsage)
            {
                 auxStr += u + ", ";
            }
        }

        auxStr = auxStr.substring(0, auxStr.length() - 2);
        auxStr += "\n";

        return auxStr;
    }

    public String getKeyUsage()
    {
        String auxStr = "";

        // System.out.println("Parseando keyUsage...");

        if (_keyUsage != null)
        {

            for (int i = 0; i < _keyUsage.length; i++)
            {
                if (_keyUsage[i])
                {
                    auxStr += _keyUsageStr[i] + ", ";
                    // System.out.println("pillado usage: " + auxStr);
                }
            }
            auxStr = auxStr.substring(0, auxStr.length() - 2);
        }

        if (_extKeyUsage != null)
        {
            for (String u : _extKeyUsage)
            {
                if (u.equals("1.3.6.1.5.5.7.3.2"))
                {
                    _emailProtection = true;
                }
            }
        }

        return auxStr;
    }

    public boolean isEmailProtectionCertificate()
    {
        return _emailProtection;
    }

    public boolean isDigitalSignatureCertificate()
    {
        if (_keyUsage != null)
            return _keyUsage[0];
        else
            return false;
    }

    public boolean isNonRepudiationCertificate()
    {
        if (_keyUsage != null)
            return _keyUsage[1];
        else
            return false;
    }

    public boolean isPKCS11Provider()
    {
        System.out.println("STORE: " + getStoreName());
        if (getStoreName().equals(SupportedKeystore.PKCS11))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /*
     * Kept out for compatibility reasons
     */
    public boolean isClauerProvider()
    {
        if (getStoreName().equals(SupportedKeystore.CLAUER))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public IKeyStore getKeyStore()
    {
        return _iksh;
    }

    public SupportedKeystore getStoreName()
    {
        return _storeName;
    }

    public String getTokenName()
    {
        return _tokenName;
    }

    public String toString()
    {
        String kuAux = getKeyUsage();

        if (kuAux.equals(""))
            return _SubjectCN;
        else
            return _SubjectCN + " (" + getKeyUsage() + ")";
    }
}
