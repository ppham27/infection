import argparse
import json

parser = argparse.ArgumentParser(description="Make graph.json for visualization.")
parser.add_argument('user_file', type=str,
                    help="The user database file.")
parser.add_argument('infected_components', type=str,
                    help="The output from the java Infector program.")
args = parser.parse_args()

# read in graph
node_to_idx = dict()
links = list()
with open(args.user_file) as f:
    num_nodes_links = f.readline().rstrip('\n').split(' ')
    N = int(num_nodes_links[0])
    M = int(num_nodes_links[1])
    for i in range(N):
        name = f.readline().rstrip('\n')
        node_to_idx[name] = {'id': i, 'name': name, 'infect': 0}
    for i in range(M):
        link = f.readline().rstrip().split(' ')
        links.append({'source': node_to_idx[link[0]]['id'], 'target': node_to_idx[link[1]]['id']})
# read in infected components
with open(args.infected_components) as f:
    component_number = 1
    for l in f:
        l = l.rstrip('\n')
        if l == "":
            break
        for name in l.split(' '):
            node_to_idx[name]['infect'] = component_number
        component_number += 1
# output as json
nodes = list(node_to_idx.values())
nodes.sort(key=lambda x : x['id'])
with open('graph.json', 'w') as f:
    output = {'nodes': nodes, "links": links}
    json.dump(output, f)

