package es.uji.security.ui.applet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uji.security.crypto.DataEncoding;
import es.uji.security.crypto.SignatureFormat;

public class SignatureConfiguration
{
    private SignatureFormat outputSignatureFormat;
    private DataEncoding outputDataEncoding;
    private DataEncoding inputDataEncoding;
    private boolean sslCertificateVerfication;
    private List<String> inputURLs;
    private List<String> outputURLs;
    private String dniToCheckCertificateAgainst;
    private Map<String, List<String>> signatureProperties;

    public SignatureConfiguration()
    {
        inputURLs = new ArrayList<String>();
        outputURLs = new ArrayList<String>();
        signatureProperties = new HashMap<String, List<String>>();
    }

    public void setOutputSignatureFormat(String format)
    {
        outputSignatureFormat = SignatureFormat.valueOf(format.toUpperCase());
    }

    public void setOutputDataEncoding(String encoding)
    {
        outputDataEncoding = DataEncoding.valueOf(encoding.toUpperCase());
    }

    public void setInputDataEncoding(String encoding)
    {
        inputDataEncoding = DataEncoding.valueOf(encoding.toUpperCase());
    }

    public void addInputURL(String inputURL)
    {
        inputURLs.add(inputURL);
    }

    public void addOutputURL(String outputURL)
    {
        outputURLs.add(outputURL);
    }

    public void setDniToCheckCertificateAgainst(String dni)
    {
        dniToCheckCertificateAgainst = dni;
    }

    public void addSignatureProperty(String propertyName, String propertyValue)
    {
        if (signatureProperties.containsKey(propertyName))
        {
            signatureProperties.get(propertyName).add(propertyValue);
        }
        else
        {
            signatureProperties.put(propertyName, Collections.singletonList(propertyValue));
        }
    }
}