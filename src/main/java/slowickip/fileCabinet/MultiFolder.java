package slowickip.fileCabinet;

import java.util.List;

public interface MultiFolder extends Folder {
  List<Folder> folders();
}
