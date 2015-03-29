/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Thibault
 */
public class Internationalization {
    public static ResourceBundle bundle;
    public static void main ()
    {
        Locale defaultLocale = new Locale("fr","FR");
                //Locale.getDefault();
        bundle = ResourceBundle.getBundle("Trad", Locale.getDefault());
    }
    
    public static String get(String t) 
    {
        Locale defaultLocale = Locale.getDefault();
        try
        {
            bundle = ResourceBundle.getBundle("Trad", Locale.getDefault());
            return bundle.getString(t);
        }
        catch (Exception te)
        {
            System.out.printf(te.getMessage());
        }
        return "";
        
    }
}
