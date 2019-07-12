package com.kodilla.royal.game.of.ur;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstructionsService {

    private static final List<String> TAGS = Arrays.asList("title", "p");

    private List<String> retrieveGameInstructions() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL instructionsFileUrl = Objects.requireNonNull(
                classLoader.getResource("instructions.xml"),
                "Accessing the game instructions file unsuccessful."
        );
        File file = new File(instructionsFileUrl.getFile());
        Path path;
        List<String> rawInstructions;
        try {
            path = Paths.get(URLDecoder.decode(file.getPath(), "UTF-8"));
            Stream<String> fileLines = Files.lines(path);
            rawInstructions = fileLines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return rawInstructions;
    }

    private List<List<String>> prepareGameInstructions(List<String> rawInstructions) {
        List<List<String>> instructions = new LinkedList<>();
        String pattern = "^<(" + String.join("|", TAGS) + ")>(.*)</\\1>$";
        Pattern instructionPattern = Pattern.compile(pattern);
        for (String rawInstruction : rawInstructions) {
            Matcher matcher = instructionPattern.matcher(rawInstruction);
            if (matcher.matches()) {
                List<String> instruction = new ArrayList<>(2);
                instruction.add(0, matcher.group(1));
                instruction.add(1, matcher.group(2));
                instructions.add(instruction);
            }
        }
        return instructions;
    }

    public void showInstructions() {
        List<String> rawInstructions = retrieveGameInstructions();
        List<List<String>> instructions = prepareGameInstructions(rawInstructions);
        InstructionsWindow instructionsWindow = new InstructionsWindow();
        instructionsWindow.setInstructions(instructions);
        instructionsWindow.show();
    }

}
