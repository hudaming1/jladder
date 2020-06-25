package org.hum.nettyproxy.test.https_client.ca.bouncycastle._1;

import java.util.Hashtable;
import java.util.Vector;

import org.bouncycastle.jce.X509Principal;

public class X509Attrs
{
    private String countryCode;
    
    private String organization;
    
    private String organizationUnit;
    
    private String emailAddress;
    
    private String localityName;
    
    private String commonName;
    
    private String giveName;
    
    private Vector v = new Vector();
    
    public X509Attrs()
    {
        v.addElement(X509Principal.C);
        v.addElement(X509Principal.CN);
        v.addElement(X509Principal.O);
        v.addElement(X509Principal.OU);
        v.addElement(X509Principal.EmailAddress);
        v.addElement(X509Principal.L);
        v.addElement(X509Principal.GIVENNAME);
        
    }
    
    public Vector getOrdering()
    {
        return v;
    }
    
    public Hashtable getAttrs()
    {
        Hashtable attrs = new Hashtable();
        if (getCountryCode() != null)
            attrs.put(X509Principal.C, getCountryCode());
        else
            v.remove(X509Principal.C);
        if (getOrganization() != null)
            attrs.put(X509Principal.O, getOrganization());
        else
            v.remove(X509Principal.O);
        if (getOrganizationUnit() != null)
            attrs.put(X509Principal.OU, getOrganizationUnit());
        else
            v.remove(X509Principal.OU);
        if (getLocalityName() != null)
            attrs.put(X509Principal.L, getLocalityName());
        else
            v.remove(X509Principal.L);
        if (getCommonName() != null)
            attrs.put(X509Principal.CN, getCommonName());
        else
            v.remove(X509Principal.CN);
        if (getEmailAddress() != null)
            attrs.put(X509Principal.EmailAddress, getEmailAddress());
        else
            v.remove(X509Principal.EmailAddress);
        if (getGiveName() != null)
            attrs.put(X509Principal.GIVENNAME, getGiveName());
        else
            v.remove(X509Principal.GIVENNAME);
        return attrs;
    }
    
    public String getCountryCode()
    {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }
    
    public String getOrganization()
    {
        return organization;
    }
    
    public void setOrganization(String organization)
    {
        this.organization = organization;
    }
    
    public String getOrganizationUnit()
    {
        return organizationUnit;
    }
    
    public void setOrganizationUnit(String organizationUnit)
    {
        this.organizationUnit = organizationUnit;
    }
    
    public String getEmailAddress()
    {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }
    
    public String getLocalityName()
    {
        return localityName;
    }
    
    public void setLocalityName(String localityName)
    {
        this.localityName = localityName;
    }
    
    public String getCommonName()
    {
        return commonName;
    }
    
    public void setCommonName(String commonName)
    {
        this.commonName = commonName;
    }
    
    public String getGiveName()
    {
        return giveName;
    }
    
    public void setGiveName(String giveName)
    {
        this.giveName = giveName;
    }
    
}
