// basic set up
var width=1024;
var height=640;
var force = d3.layout.force()
    .size([width, height])
    .charge(-400)
    .linkDistance(80)
    .on("tick", tick);
var drag = force.drag();
var svg = d3.select("#graph")
    .append("svg")
    .attr("width", width)
    .attr("height", height);
// arrowhead marker
svg.append("marker")
    .attr("id", "arrowhead")
    .attr("viewBox", "0 0 10 10")
    .attr("refX", "0")
    .attr("refY", "5")
    .attr("markerUnits", "strokeWidth")
    .attr("markerWidth", "8")
    .attr("markerHeight", "6")
    .attr("orient", "auto")
    .append("path")
    .attr("d","M 0 0 L 10 5 L 0 10 z");
// state variables
var infectionState = 0;
var numInfected = 0;
// interactive panel
var controls = d3.select("#graph").append("div");
controls
    .append("span")
    .attr("class", "button")
    .append("button")
    .attr("type", "button")
    .text("Infect")
    .on("click", infect);
controls
    .append("span")
    .attr("class", "button")
    .append("button")
    .attr("type", "button")
    .text("Reset")
    .on("click", reset);
controls.append("span")
    .attr("id", "status")
    .text("Number Infected: ")
    .append("span")
    .attr("id", "infected-number")
    .text(numInfected);
// build graph
var link = svg.selectAll(".link");
var node = svg.selectAll(".node");
var nodeLabel = svg.selectAll(".node-label");
d3.json("graph.json", function(error, graph) {
    force
        .nodes(graph.nodes)
        .links(graph.links)
        .start();
    link = link.data(graph.links)
        .enter().append("line")
        .attr("class", "link")
        .attr("marker-end","url(#arrowhead)");
    node = node.data(graph.nodes)
        .enter().append("circle")
        .attr("class", "node")
        .attr("r", 15);    
    node.call(drag);
    nodeLabel = nodeLabel.data(graph.nodes)
        .enter().append("text")
        .attr("text-anchor", "middle")
        .attr("dy", "5px")
        .text(function(d) { return d.name; });
});

// callbacks
function tick() {
    link.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.source.x + 0.75*(d.target.x - d.source.x); })
        .attr("y2", function(d) { return d.source.y + 0.75*(d.target.y - d.source.y); })
    node.attr("cx", function(d) { return d.x; })
        .attr("cy", function(d) { return d.y; });
    nodeLabel.attr("x", function(d) { return d.x; })
        .attr("y", function(d) { return d.y; });
}
function infect() {
    infectionState += 1;
    updateInfections();
}
function reset() {
    infectionState = 0;
    updateInfections();
}
function updateInfections() {
    var oldNumInfected = numInfected;
    if (infectionState === 0) {
        node.classed("infected", false);
        numInfected = 0;
    }
    numInfected = node.filter(function(d) { 
        return d.infect !== 0 && d.infect <= infectionState;
    }).classed("infected", true).size();    
    d3.select("span#infected-number")
        .transition()
        .duration(500)
        .tween("textContent", function() {
            var f = d3.interpolate(oldNumInfected, numInfected);
            return function(t)  {
                var intNumInfected = f(t);
                this.textContent = intNumInfected.toString().slice(0,3);
                if (intNumInfected === node.filter(function(d) { return d.infect > 0; }).size()) {
                    d3.select("span#infected-number").text(numInfected + ". All infected!");
                }
            };
        });   
}
