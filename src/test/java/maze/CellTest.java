package maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellTest {

    @Test
    void cellsAtSamePositionAreEqualRegardlessOfType() {
        Cell empty = new Cell(2, 3, CellType.EMPTY);
        Cell wall = new Cell(2, 3, CellType.WALL);

        assertEquals(empty, wall);
        assertEquals(empty.hashCode(), wall.hashCode());
    }

    @Test
    void cellsAtDifferentPositionsAreNotEqual() {
        assertNotEquals(
                new Cell(2, 3, CellType.EMPTY),
                new Cell(3, 2, CellType.EMPTY)
        );
    }

    @Test
    void onlyWallsAreNotWalkable() {
        for (CellType type : CellType.values()) {
            Cell cell = new Cell(0, 0, type);
            assertEquals(type != CellType.WALL, cell.isWalkable(), type.name());
        }
    }

    @Test
    void startAndEndAreDetected() {
        assertTrue(new Cell(0, 0, CellType.START).isStart());
        assertTrue(new Cell(0, 0, CellType.END).isEnd());
        assertFalse(new Cell(0, 0, CellType.EMPTY).isStart());
        assertFalse(new Cell(0, 0, CellType.EMPTY).isEnd());
    }

    @Test
    void nullTypeIsRejected() {
        assertThrows(NullPointerException.class, () -> new Cell(0, 0, null));

        Cell cell = new Cell(0, 0, CellType.EMPTY);
        assertThrows(NullPointerException.class, () -> cell.setType(null));
    }
}
