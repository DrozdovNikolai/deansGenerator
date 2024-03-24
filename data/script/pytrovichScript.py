import sys

from pytrovich.detector import PetrovichGenderDetector
from pytrovich.enums import NamePart, Gender, Case
from pytrovich.maker import PetrovichDeclinationMaker

sys.stdout.reconfigure(encoding='utf-8')

maker = PetrovichDeclinationMaker()

input_name = sys.argv[1].split(" ")

detector = PetrovichGenderDetector()
gend=detector.detect(lastname=input_name[0],firstname=input_name[1],middlename=input_name[2])
print(maker.make(NamePart.LASTNAME,gend, Case.DATIVE, input_name[0]) + " "+
      maker.make(NamePart.FIRSTNAME, gend, Case.DATIVE, input_name[1]) + " "+
      maker.make(NamePart.FIRSTNAME,gend, Case.DATIVE, input_name[2]))