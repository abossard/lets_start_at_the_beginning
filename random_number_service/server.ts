import * as express from "express/index"
import { resolve } from "path"
import { config } from "dotenv"

config({ path: resolve(__dirname, "./.env") })

const app = express();

const routes = [
    {
        route: '/api/numbers',
        name: "Output Random Numbers",
        handler: function (req, res, next) {
            res.send("hi");
        }
    }
]

routes.forEach(route => {
    app.use(route.route, route.handler)
})
app.use("/", (request, response, next) => {
    response.send(routes)
})
app.use(function(err, req, res, next){
    // whatever you want here, feel free to populate
    // properties on `err` to treat it differently in here.
    res.status(err.status || 500);
    res.send({ error: err.message });
  });
  
app.use(function(req, res){
    res.status(404);
    res.send({ error: "Lame, can't find that" });
  });

app.listen(process.env.PORT);
console.log(`Express started on port ${process.env.PORT}`);