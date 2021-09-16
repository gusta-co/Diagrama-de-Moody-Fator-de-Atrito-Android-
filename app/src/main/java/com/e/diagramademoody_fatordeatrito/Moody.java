package com.e.diagramademoody_fatordeatrito;

import java.text.DecimalFormat;

public class Moody
{

   public Double ColebrookWhiteEquation(Double Re, Double RR)
   {
       DecimalFormat df = new DecimalFormat("0.00000"); //5 decimals format
       Double f_final, f_guess, A;

       if(Re<2400) //laminar flow
       {
           return 64/Re;
       }
       else
           {
               f_guess = 0.02;
               f_final = 0.01;
               int i;

               for (i = 0; i <= 150; i++)
               {
                   String finalCheckValue = df.format(f_final);
                   String guessCheckValue = df.format(f_guess);

                   if (!finalCheckValue.equals(guessCheckValue)) //5 decimals precision
                   {
                       f_guess = f_final;
                       A = -2 * Math.log10((RR / 3.7) + (2.51 / (Re * Math.sqrt(f_guess))));
                       f_final = Math.pow((1 / A), 2);
                   } else
                   {
                       break;
                   }
               }

               if (i == 150)
               {
                   return -1.0;
               } else
               {
                   return f_final;
               }
           }
   }

}
