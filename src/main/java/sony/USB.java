package sony;

import java.io.File;

public class USB
{

    public static String getUsbDrive()
    {
        File arr$[] = File.listRoots();
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            File root = arr$[i$];
            arr$ = root.listFiles();
            len$ = arr$.length;
            for(int j$ = 0; j$ < len$; j$++)
            {
                File sony = arr$[j$];
                if(sony.isDirectory() && sony.getName().toUpperCase().equals("SONY"))
                    return root.getAbsolutePath();
            }

        }

        return "";
    }
}
