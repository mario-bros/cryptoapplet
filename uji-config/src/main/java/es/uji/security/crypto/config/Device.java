package es.uji.security.crypto.config;

public class Device
{
    private String name;
    private String library;
    private String slot;
    private boolean disableNativePasswordDialog;

    public Device()
    {
    }

    public Device(String name, String library, String slot, boolean disableNativePasswordDialog)
    {
        this.name = name;
        this.library = library;
        this.slot = slot;
        this.disableNativePasswordDialog = disableNativePasswordDialog;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLibrary()
    {
        return library;
    }

    public void setLibrary(String library)
    {
        this.library = library;
    }

    public String getSlot()
    {
        return slot;
    }

    public void setSlot(String slot)
    {
        this.slot = slot;
    }
    
    public boolean isDisableNativePasswordDialog()
    {
        return disableNativePasswordDialog;
    }

    @Override
    public String toString()
    {
        String libraryPath = library.replaceAll("\\\\", "\\\\\\\\");

        return ("name = " + name + "\r" + "library = \"" + libraryPath + "\"\r\nslot = " + slot + "\r\n");
    }
}