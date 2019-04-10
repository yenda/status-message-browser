create an account on geth if you don't have one:
```
geth account new
```

run geth and swarm with your account
```
./go/bin/geth --syncmode=light --password <(echo password) --unlock 63410f8acabd08648c9230be91f87a24e7871616 --cache=4096 --ws --wsport 8546 --wsorigins "*"

./go/bin/swarm --ens-api $HOME/.ethereum/geth.ipc --bzzaccount 63410f8acabd08648c9230be91f87a24e7871616 --corsdomain '*' --password <(echo password)
```

install npm packages and run shadow-cljs to compile and server the project
```
npm install
npx shadow-cljs watch app

```

With cider `cider-connect-cljs` then select `shadow-cljs` and type `app` as project name to run
In the REPL calling `(download @contenthash)` will fetch the first message and recursively all of its parents
