def get_data():
    m = open("AllMappingUpToHundred.txt", "r")
    count = 0
    types = {}
    challenges = {}
    for j in m.readlines():
        z = j.split()
        types[count]  = (count, z[0], float(z[1]))
        challenges[count] = []
        count += 1
    m.close()
    for i in [j+1 for j in xrange(7)]:
        if i != 4 and i!=2:
            m = open("%d.txt" % i, "r")
            count = 0
            for k in m.readlines():
                z = k.split()
                challenges[count].append((i, int(z[0]), int(z[1])))
                count+=1
            m.close()
    return challenges, types

def rank(arr, t, player):
    contest = arr[t]
    rank = 1
    for j in contest:
        if player == j[0]:
            score = j[1]
    for other in contest:
        if other[0] != player and other[1] > score:
            rank+=1
    return rank

def analyze():
    c, t = get_data()
    ranks_overall = {}
    compared_to_n = {}
    maps_overall = {}
    for i in c:
        for j in c[i]:
            if j[0] not in ranks_overall:
                ranks_overall[j[0]] = []
                compared_to_n[j[0]] = []                
                maps_overall[j[0]] = {}
            if t[i][1] not in maps_overall[j[0]]:
                maps_overall[j[0]][t[i][1]] = []
            ranks_overall[j[0]].append(rank(c,i,j[0]))
            maps_overall[j[0]][t[i][1]].append((rank(c,i,j[0]), j[1]/t[i][2]))
            compared_to_n[j[0]].append(j[1]/ t[i][2])

    return ranks_overall, compared_to_n, maps_overall


def show_analysis():
    import pylab
    r, c, t = analyze()
    print r, c, t
    maps = {}
    for i in t:
        count = 1
        bins = {}
        for m in t[i]:
            bins = pylab.hist([z[1] for z in t[i][m]], bins=60)
            if m not in maps:
                maps[m] = {}
            maps[m][i] = bins
    pylab.clf()
    for m in maps:
        for i in maps[m]:
            print maps[m][i][0]
            pylab.plot([float(j) * 10/6 for j in xrange(60)], maps[m][i][0],  label="group %d" % i)
        pylab.ylim([0, 30])
        pylab.xlim([0, 100])
        pylab.xlabel("query size (% of N)")
        pylab.ylabel("amount")
        pylab.legend(loc=2)
        pylab.title("scores for %s map" % m)
        pylab.savefig("scorefor%s.png" % m)
        pylab.show()
        #pylab.clf()

if __name__ == "__main__":
    show_analysis()
