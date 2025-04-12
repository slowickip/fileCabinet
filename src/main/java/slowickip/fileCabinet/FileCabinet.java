package slowickip.fileCabinet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileCabinet implements Cabinet {

  private final List<Folder> flatFolders = new ArrayList<>();

  @Override
  public Optional<Folder> findFolderByName(String name) {
    return flatFolders.stream()
        .filter(f -> f.name().equals(name))
        .findFirst();
  }

  @Override
  public List<Folder> findFoldersBySize(String size) {
    FolderSize folderSize;
    try {
      folderSize = FolderSize.valueOf(size.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Folder size must be SMALL, MEDIUM or LARGE", ex);
    }

    return flatFolders.stream()
        .filter(f -> f.size().equals(folderSize.name()))
        .toList();
  }

  @Override
  public int count() {
    return flatFolders.size();
  }

  public List<Folder> getFlatFolders() {
    return List.copyOf(flatFolders);
  }

  public void setFolders(List<Folder> folders) {
    flatFolders.clear();
    addFlatFolders(folders, flatFolders);
  }

  public void addFolders(List<Folder> folders) {
    addFlatFolders(folders, flatFolders);
  }

  public void addFolder(Folder folder) {
    addFolders(List.of(folder));
  }

  public void removeFolder(Folder folder) {
    flatFolders.remove(folder);
  }

  private void addFlatFolders(List<Folder> folders, List<Folder> flatFolders) {
    folders.forEach(folder -> {
      if (folder instanceof MultiFolder multiFolder) {
        flatFolders.add(multiFolder);
        addFlatFolders(multiFolder.folders(), flatFolders);
      } else if (folder != null) {
        flatFolders.add(folder);
      }
    });
  }

  enum FolderSize {
    SMALL,
    MEDIUM,
    LARGE
  }
}