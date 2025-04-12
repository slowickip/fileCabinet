package slowickip.fileCabinet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileCabinetTest {

  record SimpleFolder(String name, String size) implements Folder {
  }

  record SimpleMultiFolder(String name, String size, List<Folder> folders) implements MultiFolder {
  }

  private FileCabinet fileCabinet;

  @BeforeEach
  void setUp() {
    fileCabinet = new FileCabinet();
  }

  @Test
  void testSetFolders_EmptyList() {
    // given
    List<Folder> emptyFolders = List.of();

    // when
    fileCabinet.setFolders(emptyFolders);

    // then
    assertEquals(0, fileCabinet.count());
  }

  @Test
  void testAddAndCountFolders() {
    // given
    Folder folder1 = new SimpleFolder("Folder1", "SMALL");
    Folder folder2 = new SimpleFolder("Folder2", "MEDIUM");

    // when
    fileCabinet.addFolder(folder1);
    fileCabinet.addFolder(folder2);

    // then
    assertEquals(2, fileCabinet.count());
  }

  @Test
  void testFindFolderByName() {
    // given
    Folder folder1 = new SimpleFolder("Folder1", "SMALL");
    Folder folder2 = new SimpleFolder("Folder2", "MEDIUM");
    fileCabinet.setFolders(List.of(folder1, folder2));

    // when
    Optional<Folder> found = fileCabinet.findFolderByName("Folder1");
    Optional<Folder> notFound = fileCabinet.findFolderByName("NonExistentFolder");

    // then
    assertTrue(found.isPresent(), "Folder1 should be found");
    assertEquals("Folder1", found.get().name());
    assertFalse(notFound.isPresent(), "NonExistentFolder should not be found");
  }

  @Test
  void testFindFoldersBySize() {
    // given
    Folder folder1 = new SimpleFolder("Folder1", "SMALL");
    Folder folder2 = new SimpleFolder("Folder2", "SMALL");
    Folder folder3 = new SimpleFolder("Folder3", "LARGE");
    fileCabinet.setFolders(List.of(folder1, folder2, folder3));

    // when
    List<Folder> smallFolders = fileCabinet.findFoldersBySize("small");

    // then
    assertEquals(2, smallFolders.size(), "There should be exactly two SMALL folders");
    smallFolders.forEach(folder -> assertEquals("SMALL", folder.size()));
  }

  @Test
  void testFindFoldersByIncorrectSize() {
    // given
    Folder folder1 = new SimpleFolder("Folder1", "SMALL");
    Folder folder2 = new SimpleFolder("Folder2", "MEDIUM");
    fileCabinet.setFolders(List.of(folder1, folder2));

    // when && then
    assertThrows(IllegalArgumentException.class, () -> fileCabinet.findFoldersBySize("EXTRA_LARGE"));
  }

  @Test
  void testFlatFolderWithMultiFolder() {
    // given
    Folder child1 = new SimpleFolder("Child1", "MEDIUM");
    Folder child2 = new SimpleFolder("Child2", "LARGE");
    MultiFolder parentMultiFolder = new SimpleMultiFolder("ParentMulti", "SMALL", List.of(child1, child2));
    fileCabinet.setFolders(List.of(parentMultiFolder));

    // when
    int count = fileCabinet.count();
    Optional<Folder> foundParent = fileCabinet.findFolderByName("ParentMulti");
    Optional<Folder> foundChild1 = fileCabinet.findFolderByName("Child1");
    Optional<Folder> foundChild2 = fileCabinet.findFolderByName("Child2");

    // then
    assertEquals(3, count, "Flattened structure should contain 3 folders in total");
    assertTrue(foundParent.isPresent(), "ParentMulti should be found");
    assertTrue(foundChild1.isPresent(), "Child1 should be found");
    assertTrue(foundChild2.isPresent(), "Child2 should be found");
  }

  @Test
  void testFlatFolderWithMultiFolderContainingNull() {
    // given
    List<Folder> childFolders = new ArrayList<>();
    Folder child1 = new SimpleFolder("Child1", "MEDIUM");
    Folder child2 = null;
    childFolders.add(child1);
    childFolders.add(child2);
    MultiFolder parentMultiFolder = new SimpleMultiFolder("ParentMulti", "SMALL", childFolders);
    fileCabinet.setFolders(List.of(parentMultiFolder));

    // when
    int count = fileCabinet.count();
    Optional<Folder> foundParent = fileCabinet.findFolderByName("ParentMulti");
    Optional<Folder> foundChild1 = fileCabinet.findFolderByName("Child1");
    Optional<Folder> foundChild2 = fileCabinet.findFolderByName("Child2");

    // then
    assertEquals(2, count, "Flattened structure should contain 2 folders (ignoring null)");
    assertTrue(foundParent.isPresent(), "ParentMulti should be found");
    assertTrue(foundChild1.isPresent(), "Child1 should be found");
    assertFalse(foundChild2.isPresent(), "Child2 should not be found as it is null");
  }

  @Test
  void testRemoveFolder() {
    // given
    Folder folder1 = new SimpleFolder("Folder1", "SMALL");
    Folder folder2 = new SimpleFolder("Folder2", "MEDIUM");
    fileCabinet.setFolders(List.of(folder1, folder2));

    // when
    fileCabinet.removeFolder(folder1);
    Optional<Folder> found = fileCabinet.findFolderByName("Folder1");

    // then
    assertEquals(1, fileCabinet.count(), "After removal, there should be 1 folder");
    assertFalse(found.isPresent(), "Folder1 should have been removed");
  }

  @Test
  void testGetFolders() {
    // given
    Folder folder1 = new SimpleFolder("Folder1", "SMALL");
    Folder folder2 = new SimpleFolder("Folder2", "MEDIUM");
    fileCabinet.setFolders(List.of(folder1, folder2));

    // when
    List<Folder> folders = fileCabinet.getFlatFolders();

    // then
    assertEquals(2, folders.size(), "There should be exactly 2 folders");
    assertTrue(folders.contains(folder1), "Folders should contain Folder1");
    assertTrue(folders.contains(folder2), "Folders should contain Folder2");
  }
}

