package Game2048;

import java.awt.*;

public class Tile {
    int value = 0;
    Tile(){}
    Tile(int num) {value = num;}

    public boolean isEmpty() {return value == 0; }

    Color getFontColor() {
        return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
    }

    Color getTileColor() {
        switch(value) {
            case 0: return new Color(0x57E2E5);
            case 2: return new Color(0xedcf7);
            case 4: return new Color(0x47BBF1);
            case 8: return new Color(0x048CCB);
            case 16: return new Color(0x4662D7);
            case 32: return new Color(0xf67c5);
            case 64: return new Color(0x0747A6);
            case 128: return new Color(0x64EFE2);
            case 256: return new Color(0x1BCEBB);
            case 512: return new Color(0x1CE791);
            case 1024: return new Color(0x1FD25A);
            case 2048: return new Color(0x037A0D);

            default: return new Color(0x0618D9);
        }
    }

}
