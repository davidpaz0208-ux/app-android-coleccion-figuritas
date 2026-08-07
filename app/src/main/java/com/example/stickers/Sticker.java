    package com.example.stickers;

    import java.io.Serializable;

    public class Sticker implements Serializable {

        private int id;
        private int number;
        private String name;
        private String team;
        private String rarity;
        private boolean repeated;

        public Sticker() { }

        public Sticker(int id, int number, String name, String team, String rarity, boolean repeated) {
            this.id = id;
            this.number = number;
            this.name = name;
            this.team = team;
            this.rarity = rarity;
            this.repeated = repeated;
        }

        public int getId() { return id; }
        public int getNumber() { return number; }
        public String getName() { return name; }
        public String getTeam() { return team; }
        public String getRarity() { return rarity; }
        public boolean isRepeated() { return repeated; }

        public void setId(int id) { this.id = id; }
        public void setNumber(int number) { this.number = number; }
        public void setName(String name) { this.name = name; }
        public void setTeam(String team) { this.team = team; }
        public void setRarity(String rarity) { this.rarity = rarity; }
        public void setRepeated(boolean repeated) { this.repeated = repeated; }

        public int getPrice() {
            switch (rarity) {
                case "Legend": return 5000;
                case "Rare": return 2000;
                default: return 500;
            }
        }

        @Override
        public String toString() {
            return "#" + number + " - " + name + " (" + team + ")";
        }
    }