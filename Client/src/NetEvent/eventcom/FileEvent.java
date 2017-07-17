package NetEvent.eventcom;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by XHWhy on 2017/7/9.
 */
public class FileEvent extends NetEvent {

    private ArrayList<String> filenames;
    private ArrayList<Boolean> success;
    private ArrayList<Integer> retCode;
    private boolean upload;
    private int size;
    private boolean contentPic;
    private long questionID;

    public FileEvent(ArrayList<String> filenames, ArrayList<Boolean> success, ArrayList<Integer> retCode, boolean upload, boolean contentPic, long questionID)
    {
        super(EventType.FILE_EVENT);
        if(filenames.size() != success.size() || filenames.size() != retCode.size()) {
            this.size = -1;
        } else {
            this.size = filenames.size();
            this.filenames = filenames;
            this.success = success;
            this.retCode = retCode;
            this.upload = upload;
            this.contentPic = contentPic;
            this.questionID = questionID;
        }
    }

    public ArrayList<String> getAllFileName() {return this.filenames;}
    public String getFilename(int i) {
        if (size > i)
            return filenames.get(i);
        else return null;
    }
    public ArrayList<Boolean> getAllSuccess() {return this.success;}
    public Boolean getAllFilename(int i) {
        if(size > i)
            return success.get(i);
        else return null;
    }
    public ArrayList<Integer> getAllRetCode() {return this.retCode;}
    public Integer getRegCode(int i) {
        if(size > i)
            return retCode.get(i);
        else
            return null;
    }
    public int getSize() {return size;}
    public boolean isUpload() {return this.upload;}
    public long getQuestionID() {return questionID;}
    public boolean isContentPic() {return contentPic;}
}
