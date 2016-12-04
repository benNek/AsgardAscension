package main.java.utils;

import java.util.ArrayList;
import java.util.List;

public class Pager {
	 
    static final int PAGELENGTH = 5;
 
    public Pager()
    {
        List<String> l = new ArrayList<String>();
     
        //generates a demo List with some Strings
        for(int i=0 ; i<18 ; i++)
        {
            l.add(new StringBuilder("string").append(i+1).toString());
        }
     
        //Fetches a page and displays it afterwards
        displayList(getPage(l, 4));
     
    }
 
    /**
    *
    * @param l A list containing all Strings,
    * @param pagenr The page number
    * @return List<String> containing all Strings on the page
    */
    public List<String> getPage(List<String> l, int pagenr)
    {
        List<String> page = new ArrayList<String>();
     
        int listart = (pagenr - 1) * PAGELENGTH;
        int liend  = listart + PAGELENGTH;
     
        for(int i=listart ; i<liend ;i++)
        {
            if(i < l.size())
            {
                page.add(l.get(i));
            }
            else
            {
                break;
            }
        }
     
        return page;
    }
 
    /**
    * Displays a List<String> for debugging.
    * @param l
    */
    public void displayList(List<String> l)
    {
        for(int i=0 ; i<l.size() ; i++)
        {
            System.out.println(l.get(i));
        }
    }
}