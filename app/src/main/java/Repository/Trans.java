package Repository;

import kotlin.text.UStringsKt;

public class Trans {

    public static String db_name="VideoPP";
    public static final Integer VERSION = 1;
    public static final String TBL_VIDEO = "video";
    public static final String ID = "id";
    public static final String VIDEO = "video";


    public static final String CREATE_TABLE= "CREATE TABLE " + TBL_VIDEO +
            " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + VIDEO + " TEXT)";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TBL_VIDEO;
}
