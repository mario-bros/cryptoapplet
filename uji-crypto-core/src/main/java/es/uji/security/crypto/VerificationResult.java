package es.uji.security.crypto;

import java.util.ArrayList;
import java.util.List;

public class VerificationResult
{
    private boolean valid;
    private List<String> errors;
    
    public VerificationResult()
    {
        this.valid = false;
        this.errors = new ArrayList<String>();
    }
    
    public VerificationResult(boolean valid, List<String> errors)
    {
        this.valid = valid;
        this.errors = errors;
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    public void setValid(boolean valid)
    {
        this.valid = valid;
    }
    
    public List<String> getErrors()
    {
        return errors;
    }
    
    public String[] getErrorsAsStringArray()
    {
    	String[] aux= new String[errors.size()];
    	for (int i=0; i<aux.length; i++){
    		aux[i]= (String) errors.get(i); 
    	}
        return aux;
    }
    
    public void setErrors(ArrayList<String> errors)
    {
        this.errors = errors;
    }
    
    public void addError(String error)
    {
        if (this.errors != null)
        {
            this.errors.add(error);
        }
    }
}
