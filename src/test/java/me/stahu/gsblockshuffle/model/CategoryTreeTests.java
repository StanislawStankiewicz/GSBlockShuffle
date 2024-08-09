package me.stahu.gsblockshuffle.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryTreeTests {

    @Test
    void getBlocks_returnsAllIncludedBlocks() {
        Category category1 =
                new Category("category1", true, 1, Set.of(), Set.of(new Block("block1")));
        Category category2 =
                new Category("category2", false, 1, Set.of(), Set.of(new Block("block2")));
        Category category3 =
                new Category("category3", true, 1, Set.of(), Set.of(new Block("block3")));
        CategoryTree categoryTree = new CategoryTree(Set.of(category1, category2, category3));

        Set<Block> includedBlocks = categoryTree.getBlocks();
        for (Block block : includedBlocks) {
            System.out.println(block.getNames());
        }

        assertThat(includedBlocks).containsExactlyInAnyOrder(new Block("block1"), new Block("block3"));
    }

    @Test
    void testDumpYaml() throws IOException {
        Path tempFile = Files.createTempFile("test", ".yaml");
        String filePath = tempFile.toString();

        Category category4 =
                new Category("category4", true, 1, Set.of(), Set.of(new Block("block4")));
        Category category3 =
                new Category("category3", true, 1, Set.of(), Set.of(new Block("block3")));
        Category category2 =
                new Category("category2", false, 1, Set.of(category4), Set.of());
        Category category1 =
                new Category("category1", true, 1, Set.of(category2, category3), Set.of());

        CategoryTree categoryTree = new CategoryTree(Set.of(category1));

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("expectedYml.yaml");
        assert inputStream != null;
        String expectedYaml = new String(inputStream.readAllBytes());

        categoryTree.dumpYaml(filePath);

        String output = Files.readString(tempFile);
        String normalizedExpectedYaml = expectedYaml.replaceAll("\\s+", "");
        String normalizedActualYaml = output.replaceAll("\\s+", "");
        assertEquals(normalizedExpectedYaml, normalizedActualYaml);

        Files.delete(tempFile);
    }
}
