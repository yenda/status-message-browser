
```
npm install
npx shadow-cljs watch app
./go/bin/geth --syncmode=light
./go/bin/swarm --ens-api $HOME/.ethereum/geth.ipc --bzzaccount 63410f8acabd08648c9230be91f87a24e7871616 --corsdomain '*'
npx shadow-cljs watch app

```


In the REPL calling `(download @contenthash)` will fetch the first message and recursively all of its parents
