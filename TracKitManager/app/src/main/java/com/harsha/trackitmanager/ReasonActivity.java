package com.harsha.trackitmanager;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Harsha on 05/12/16.
 */
public class ReasonActivity {
    public void act()
    {
        ArrayList<String> TimeDuration = new ArrayList<String>();
        ArrayList<String> VibsSts = new ArrayList<String>();
       for(int i=1;i<24;i++)
       {
           TimeDuration.add(i+"29:05 ");
       }
        for(int j=0;j<TimeDuration.size();j++)
        {
            VibsSts.add("HIGH");
        }

        try {
            DateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd kk:mm:ss");
            String firstStr = "2015-10-20 03:05:13";
            String secondStr = "2015-10-20 03:05:13";
            Date first = sdf.parse(firstStr);
            Date second = sdf.parse(secondStr);
            boolean after = (first.before(second));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
}
    private int Calc_Equipment_Utilization(int c,int i)
    {
        return (i/c)*100;
    }
    private int Process_Equipment_Utilization(int k,int c)
    {
        return (k/c)*100;
    }
    private int Potential_Equipment_Utilization(int e,int c)
    {
        return (e/c)*100;
    }
    private int Lost_Capacity(int f,int h,int j,int e)
    {
        return ((f+h+j)/e)*100;
    }

}
