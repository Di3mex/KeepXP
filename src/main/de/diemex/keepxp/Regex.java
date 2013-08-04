package de.diemex.keepxp;


import java.util.regex.Pattern;

/**
 * @author Diemex
 */
public class Regex
{
    private static Pattern onlyNums = Pattern.compile("[^0-9]");
    private static Pattern onlyEnum = Pattern.compile("[^A-Z_]");


    public static int parseNumber(String input)
    {
        int num;
        input = onlyNums.matcher(input).replaceAll("");
        if (input.length() > 0)
            num = Integer.parseInt(input);
        else
            throw new NumberFormatException("Not a readable number \"" + input + "\"");
        return num;
    }


    public static String stripEnum(String input)
    {
        input = input.toUpperCase();
        input = onlyEnum.matcher(input).replaceAll("");
        return input;
    }
}
