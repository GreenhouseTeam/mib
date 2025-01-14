package dev.greenhouseteam.mib.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

import java.util.Optional;

public record NoteWithOctave(MibNote key, int octave) {

    public static final int DEFAULT_OCTAVE = 3;
    private static final int MIN_OCTAVE = 1;
    private static final int MAX_OCTAVE = 4;

    public static final NoteWithOctave DEFAULT = new NoteWithOctave(MibNote.C, DEFAULT_OCTAVE);

    public static final Codec<NoteWithOctave> CODEC = Codec.STRING.flatXmap(string -> {
        if (!string.matches(".[0-9]") && !string.matches("..[0-9]"))
            return resultOrError(string, Optional.of(DEFAULT_OCTAVE));
        return resultOrError(string, Optional.empty());
        }, octaveKey -> DataResult.success(octaveKey.key.getSerializedName() + octaveKey.octave));

    private static DataResult<NoteWithOctave> resultOrError(String string, Optional<Integer> octave) {
        if (string.length() > 3)
            return DataResult.error(() -> "Notes may not have more than 3 characters.");
        boolean isSharp = string.length() >= 2 && string.charAt(1) == '#';
        try {
            NoteWithOctave keyWithOctave = new NoteWithOctave(MibNote.getNote(isSharp ? string.substring(0, 2) : string.substring(0, 1)), Mth.clamp(octave.orElseGet(() -> Character.getNumericValue(isSharp ? string.charAt(2) : string.charAt(1))), MIN_OCTAVE, MAX_OCTAVE));
            return DataResult.success(keyWithOctave);
        } catch (Exception ignored) {}
        return DataResult.error(() -> "Could not get note from '" + string + "'. Must be one of: " + MibNote.buildValuesString() + ", optionally with an octave ranging from  " + MIN_OCTAVE + "-" + MAX_OCTAVE + " at the end.");
    }

    public int getValue() {
        return key.ordinal() + ((octave - 1) * 12);
    }

    public float getPitchFromNote() {
        return getPitchFromNote(DEFAULT);
    }

    public float getPitchFromNote(NoteWithOctave startingKey) {
        return (float)Math.pow(2.0, (((double)getValue()) - startingKey.getValue()) / 12);
    }

    public static NoteWithOctave fromInt(int value) {
        int currentOctave = 1;
        for (int i = 0; i < MibNote.values().length * MAX_OCTAVE; ++i) {
            if (i == value)
                return new NoteWithOctave(MibNote.values()[i % 12], currentOctave);
            if (i % 12 == 11)
                ++currentOctave;
        }
        throw new RuntimeException("Int must be within a range of 0-" + MibNote.values().length * MAX_OCTAVE + ".");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NoteWithOctave other))
            return false;
        return other.octave == octave && other.key == key;
    }
}