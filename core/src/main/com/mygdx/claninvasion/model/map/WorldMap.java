package com.mygdx.claninvasion.model.map;


import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Map modal of the application
 * Should represent all the map manipulations and connected to the view libgdx tilemap
 * @version 0.01
 * TODO Logic implementation required
 */
public class WorldMap {
    private final ArrayList<WorldCell> worldCells;
    private TiledMapTileLayer entitiesLayer;
    private Graph G;

    /**
     * @param worldCells - array of worldCells
     */
    public WorldMap(WorldCell[] worldCells) {
        this.worldCells = new ArrayList<>();
        this.worldCells.addAll(Arrays.asList(worldCells));
    }

    public WorldMap() {
        this.worldCells = new ArrayList<>();
    }

    public void addCell(WorldCell worldCell) {
        this.worldCells.add(worldCell);
    }

    public void clear() {
        this.worldCells.clear();
    }

    public int getSize() {
        return this.worldCells.size();
    }

    public WorldCell getCell(int i) {
        return worldCells.get(i);
    }

    public WorldCell getCell(Pair<Integer, Integer> cellPlace) {
        return (WorldCell) worldCells.stream().filter(cell -> cell.getMapPosition().equals(cellPlace)).toArray()[0];
    }
    public void setEntitiesLayer(TiledMapTileLayer entitiesLayer) {
        this.entitiesLayer = entitiesLayer;
    }
    public TiledMapTileLayer getLayer2() {
       return  this.entitiesLayer ;
    }

    public int maxCellRowPosition() {
        return worldCells
                .stream()
                .max((c1, c2) -> c1.getMapPosition().getValue0() >= c2.getMapPosition().getValue0() ? 1 : -1)
                .get()
                .getMapPosition()
                .getValue0();
    }

    public int minCellRowPosition() {
        return worldCells
                .stream()
                .min((c1, c2) -> c1.getMapPosition().getValue0() >= c2.getMapPosition().getValue0() ? 1 : -1)
                .get()
                .getMapPosition()
                .getValue0();
    }

    public int maxCellColumnPosition() {
        return worldCells
                .stream()
                .max((c1, c2) -> c1.getMapPosition().getValue1() >= c2.getMapPosition().getValue1() ? 1 : -1)
                .get()
                .getMapPosition()
                .getValue1();
    }

    public int minCellColumnPosition() {
        return worldCells
                .stream()
                .min((c1, c2) -> c1.getMapPosition().getValue1() >= c2.getMapPosition().getValue1() ? 1 : -1)
                .get()
                .getMapPosition()
                .getValue1();
    }

    public ArrayList<WorldCell> getCells() {
        return worldCells;
    }

    public void printEntitiesLayer() {
        for (int i = minCellRowPosition(); i <= maxCellRowPosition(); i++) {
            System.out.print("{");
            for (int j = minCellColumnPosition(); j <= maxCellColumnPosition(); j++) {
                WorldCell cell = getCell(new Pair<>(i, j));
                if (cell.getOccupier() != null) {
                    System.out.print(" " + cell.getOccupier().getSymbol().letter + ", ");
                } else {
                    System.out.print(" -, ");
                }
            }
            System.out.print("}\n");
        }
    }

    public void mutate(WorldCell cell1, WorldCell cell2) {
        Pair<Integer, Integer> coordinates = new Pair<>(cell1.getMapPosition().getValue1(), cell1.getMapPosition().getValue0());
        Pair<Integer, Integer> coordinatedDist = new Pair<>(cell2.getMapPosition().getValue1(), cell2.getMapPosition().getValue0());
        TiledMapTileLayer.Cell cell = entitiesLayer.getCell(coordinates.getValue0(), coordinates.getValue1());
        entitiesLayer.setCell(coordinates.getValue0(), coordinates.getValue1(), null);
        entitiesLayer.setCell(coordinatedDist.getValue0(), coordinatedDist.getValue1(), cell);
    }

    public void mutate(int index1, int index2) {

        if (index1 < 0 || index1 >= getSize()) {
            throw new IllegalArgumentException("First argument has index out of bounds");
        }

        if (index2 < 0 || index2 >= getSize()) {
            throw new IllegalArgumentException("Second argument has index out of bounds");
        }

        WorldCell cell1 = getCell(index1);
        WorldCell cell2 = getCell(index2);

        mutate(cell1, cell2);


    }

    public Graph getGraph(){
        return this.G;
    }
    public void setGraph(int size, ArrayList<WorldCell> worldCells){
        System.out.println(size);
        this.G = new Graph(size , worldCells);

    }
    /**
     * Change the containment of the c1 (possible decease
     * of the entity which populated it)
     * @param cell - cell to remove the entity
     */
   /* public void mutate(WorldCell cell) {
        AtomicReference<Pair<Integer, Integer>> coordinates = new AtomicReference<>(cell.getMapPosition());
        Timer.schedule(new Timer.Task() {
            int i = 7;
            int count = 0;
            @Override
            public void run() {
                TiledMapTileLayer.Cell cell = entitiesLayer.getCell(coordinates.get().getValue0() + count, coordinates.get().getValue1());
                entitiesLayer.setCell(i + count, 4, null);
                entitiesLayer.setCell(i + 1 + count, 4, cell);
                count++;
            }
        }, 2, 1, 20);

    }*/
}
