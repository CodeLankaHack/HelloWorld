package com.harsha.trackit;

public class Config {

	//public String ip="ry3281i62o.database.windows.net";
	public String ip="*****";
	public String db="******";
    public String uName="******";
    public int threadDelay=10000;
    public String pwd="*******";
    
    public int getdelay()
    {
        return threadDelay;
    }

    public void setdelay(int value)
    {
    	threadDelay = value;
    }
    
    public String getip()
    {
        return ip;
    }

    public void setip(String value)
    {
    	ip = value;
    }

    public String getdb()
    {
        return db;
    }

    public void setdb(String value)
    {
    	db = value;
    }
    public String getuName()
    {
        return uName;
    }

    public void setuName(String value)
    {
    	uName = value;
    }
    public String getpwd()
    {
        return pwd;
    }

    public void setpwd(String value)
    {
    	pwd = value;
    }
	
}
