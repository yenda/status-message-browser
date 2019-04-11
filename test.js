import BzzAPI from '@erebos/api-bzz-node';
import { pubKeyToAddress } from '@erebos/keccak256';
import { createKeyPair, sign } from '@erebos/secp256k1';

const BZZ_URL = 'http://localhost:8500';

// This setup is meant for demonstration purpose only - keys and signatures security must be handled by the application
const keyPair = createKeyPair("facadefacadefacadefacadefacadefacadefacadefacadefacadefacadefaca");
const user = pubKeyToAddress(keyPair.getPublic().encode());
const signBytes = async bytes => sign(bytes, keyPair.getPrivate());
const bzz = new BzzAPI({ url: BZZ_URL, signBytes });

// This function can be called any time the website contents change
const publishContents = async contents => {
    // uploadFeedValue() uploads the given contents and updates the feed to point to the contents hash
    const feedHash = await bzz.createFeedManifest({
        user,
        name: 'a good topic name',
    })
    console.log("feed", feedHash);
    const res = await bzz.updateFeedValue(feedHash, contents);
    return res;
};

// Example use of publishContents()
const setupContents = async () => {
    const res = await publishContents("This hour's update: Swarm 99.0 has been released!");
    console.log("hello", res);
};

setupContents();
