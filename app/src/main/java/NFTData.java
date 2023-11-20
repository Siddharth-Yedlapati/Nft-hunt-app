import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NFTData {

    private static DatabaseReference mDatabase;

    public static void main(String[] args) {

        // initialize Firebase app
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setServiceAccount("Firebase_Secret.json")
            .setDatabaseUrl("https://console.firebase.google.com/project/nft-hunt-app/")
            .build();
        FirebaseApp.initializeApp(options);

        // get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // store NFT data
        NFT nft1 = new NFT("NFT-1", 12.843642, ,77.66326,"https://drive.google.com/file/d/1GksFqpwVfzTIWfrtA0eI7SUIdC9CcSzB/view?usp=share_link");
        NFT nft2 = new NFT("NFT-2", 12.843698, ,77.67046,"https://drive.google.com/file/d/1CFfjaZQbG1pfTUeUR_M0YiRcaJcRq0Sn/view?usp=share_link");
        NFT nft3 = new NFT("NFT-3", 12.843608, 77.67189,"https://drive.google.com/file/d/1brBVuVUmcYVT0pTePPtOmKMu15BMexMb/view?usp=share_link");

        mDatabase.child("nfts").child(nft1.getId()).setValue(nft1);
        mDatabase.child("nfts").child(nft2.getId()).setValue(nft2);
        mDatabase.child("nfts").child(nft3.getId()).setValue(nft3);

    }

    public static class NFT {

        private String id;
        private double latitude;
        private double longitude;
        private String file_location;

        public NFT() {
            // default constructor required for Firebase
        }

        public NFT(String id, double latitude, double longitude, String file_location) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.file_location = file_location;
        }

        public String getId() {
            return id;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
