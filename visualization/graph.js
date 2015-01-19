// basic set up
var width=1024;
var height=640;
var nodeSize = 15;
var uninfectedFill = "#b3cde3";
var infectedFill = "#fbb4ae";
var force = d3.layout.force()
    .size([width, height])
    .linkDistance(80)
    .on("tick", tick);
var drag = force.drag()
    .on("dragstart", dragstart);
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
// legend
var legendRowHeight = 40;
var legendLabels = [{"row": 1, "label": "uninfected", "fill": uninfectedFill},
                    {"row": 2, "label": "infected", "fill": infectedFill},
                    {"row": 3, "label": "student-coach"}];
var legend = svg
    .append("g").attr("id", "legend")
    .attr("transform","translate(30 20)");
legend.selectAll(".legend-label")
    .data(legendLabels).enter()
    .append("text")
    .attr("x", 30)
    .attr("y", function(d) { return d.row*legendRowHeight; })
    .attr("text-anchor", "right")
    .attr("dy", "5px")
    .text(function(d) { return d.label; });
legend.selectAll(".legend-circle")
    .data(legendLabels.slice(0, 2)).enter()
    .append("circle")
    .attr("class", "node")
    .attr("cy", function(d) { return d.row*legendRowHeight; })
    .attr("r", nodeSize)
    .attr("fill", function(d) { return d.fill; });
legend
    .append("line")
    .attr("class", "link")
    .attr("x1", -nodeSize).attr("y1", legendRowHeight*3)
    .attr("x2", nodeSize-5).attr("y2", legendRowHeight*3)
    .attr("marker-end", "url(#arrowhead)");

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
    .text("Users Infected: ")
    .append("span")
    .attr("id", "infected-number")
    .text(numInfected);
// build graph
var link = svg.selectAll(".link.data");
var node = svg.selectAll(".node.data");
var nodeLabel = svg.selectAll(".node-label.data");
d3.json("graph.json", function(error, graph) {
    force.charge(Math.min(-450+graph.nodes.length*5, -75));
    force
        .nodes(graph.nodes)
        .links(graph.links)
        .start();
    link = link.data(graph.links)
        .enter().append("line")
        .attr("class", "link data")
        .attr("marker-end","url(#arrowhead)");
    node = node.data(graph.nodes)
        .enter().append("circle")
        .attr("class", "node data")
        .attr("fill", uninfectedFill)
        .attr("r", nodeSize)
        .on("mouseover", mouseoverNode)
        .on("mouseout", mouseoutNode)
        .call(drag);
    nodeLabel = nodeLabel.data(graph.nodes)
        .enter().append("text")
        .attr("class", "node-label data")
        .attr("text-anchor", "middle")
        .attr("dy", "5px")
        .text(function(d) { return d.name; })
        .on("mouseover", mouseoverNodeLabel)
        .on("mouseout", mouseoutNodeLabel)
        .call(drag);
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


function dragstart(d) {
    node.each(function(dd) { dd.fixed = false; })
    d.fixed = true;
}

function mouseoverNode() {
    d3.select(this).classed("hover", true);
}

function mouseoutNode() {
    d3.select(this).classed("hover", false);
}

function mouseoverNodeLabel(d) {    
    node.filter(function(dd) { return dd.name === d.name; }).classed("hover", true);
}

function mouseoutNodeLabel(d) {
    node.filter(function(dd) { return d.name === d.name; }).classed("hover", false);    
}

function infect() {
    // if there are still users left to infect
    if (numInfected < node.filter(function(d) { return d.infect > 0; }).size()) {
        infectionState += 1;
        updateInfections();
    }
}
function reset() {
    infectionState = 0;
    updateInfections();
}
function updateInfections() {
    var transitionDuration = 1000;
    var oldNumInfected = numInfected;
    if (infectionState === 0) {
        node.transition().duration(transitionDuration).attr("fill", uninfectedFill);
        numInfected = 0;
    } else {
        numInfected = node.filter(function(d) { 
            return d.infect !== 0 && d.infect <= infectionState;
        }).transition().duration(transitionDuration).attr("fill", infectedFill).size();
    }
    // number text transition
    d3.select("span#infected-number")
        .transition()
        .duration(transitionDuration)
        .tween("textContent", function() {
            var f = d3.interpolate(oldNumInfected, numInfected);
            return function(t)  {
                var intNumInfected = f(t);
                this.textContent = intNumInfected.toFixed(0);
                if (intNumInfected === node.filter(function(d) { return d.infect > 0; }).size()) {
                    d3.select("span#infected-number").text(numInfected + ". All infected!");
                }
            };
        });   
}
