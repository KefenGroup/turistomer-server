    package etu.kefengroup.turistomer.entity.model;

    import lombok.*;

    import java.util.List;

    @Getter @Setter
    @AllArgsConstructor
    public class Prediction {
        private List<String> cuisine;
        private List<String> location;
        private List<String> meal;
        private List<Integer> isClose;
        private List<String> price;
        private List<Integer> isCheap;
        private List<Integer> isExpensive;
        private List<String> amenity;
        private List<String> rating;
    }