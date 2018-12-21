package com.muffinsoft.alexa.skills.samuraichef.components;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DictionaryFileLoader {

    public Map<String, List<String>> upload(String filename, Set<String> letters) throws IOException {

        Path path = definePathToFile(filename);

        Set<String> words = createStreamFromFilePath(path);

        return uploadFilesFromSet(words, letters);
    }

    private Path definePathToFile(String filename) {

        return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).getFile()).toPath();
    }


    private Set<String> createStreamFromFilePath(Path path) throws IOException {
        return Files.lines(path).collect(Collectors.toSet());
    }

    private Map<String, List<String>> uploadFilesFromSet(Set<String> words, Set<String> letters) {
        Map<String, List<String>> result = initMap(letters);
        for (String firstLetters : letters) {
            for (String word : words) {
                if (word.startsWith(firstLetters)) {
                    result.get(firstLetters).add(word);
                }
            }
        }
        return result;
    }

    private Map<String, List<String>> initMap(Set<String> letters) {
        Map<String, List<String>> result = new HashMap<>();
        for (String firstLetters : letters) {
            result.put(firstLetters, new ArrayList<>());
        }
        return result;
    }
}
