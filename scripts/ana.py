class Posting:
  def __init__(self, term, docId, position):
    self.term = term
    self.docId = int(docId)
    self.position = int(position)

def read_posts(path = '../dat/post.txt'):
  pl = dict() # posting list
  with open(path, 'r', encoding = "ISO-8859-1") as f:
    l = f.readline()
    while l:
      p = Posting(*l.split())
      if p.term not in pl:
        pl[p.term] = []
      pl[p.term].append(p)
      l = f.readline()
  return pl

# Leverage the fact that post.txt is ordered by docId and position
def read_doc(id=0, path='../dat/post.txt'):
  words = []
  with open(path, 'r', encoding="ISO-8859-1") as f:
    l = f.readline()
    while l:
      p = Posting(*l.split())
      l = f.readline()
      if p.docId == id:
        words.append(p.term)
      elif p.docId < id:
        continue
      else:
        break
  return words

doc = read_doc(id=10)
print(sorted(doc))
