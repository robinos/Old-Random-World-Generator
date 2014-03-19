/** 
  * Base class for all the GameObjects in the game world.
 **/
GameObject.java

private static final long serialVersionUID = 1L;
private int id;
private GridVector location;
private String name;
private String picture;

public GameObject(int id, GridVector location, String name, String picture);
public GameObject();

public int getID();
public void setID(int id);
public GridVector getLocation();
public void setLocationXYZ(int x, int y, int z);
public void setLocationVector(GridVector location);
public String getName();
public void setName(String name);
public String getPicture();
public void setPicture(String picture);
public void moveObject(GridVector newLocation);
public GameObject clone();
public String toString();
public boolean equals(Object obj);
public int hashCode();


/** 
  * Base class for all Creatures in the game world.
 **/
Creature.java (extends GameObject)

private static final long serialVersionUID = 13L;

//Statistics
private int base_physical = 0;
private int base_strength = 0;
private int base_agility = 0;
private int base_mental = 0;
private int base_willpower = 0;
private int base_intelligence = 0;
private int base_spiritual = 0;
private int base_spirit = 0;
private int base_charisma = 0;
private int base_awareness = 0;
private int current_physical = 0;
private int current_strength = 0;
private int current_agility = 0;
private int current_mental = 0;
private int current_willpower = 0;
private int current_intelligence = 0;
private int current_spiritual = 0;
private int current_spirit = 0;
private int current_charisma = 0;
private int current_awareness = 0;

//Base pools and threshholds
private int max_body = 100;
private int max_mind = 100;
private int max_soul = 100;
private int current_body = 100;
private int current_mind = 100;
private int current_soul = 100;
private int base_physical_threshhold = 0;
private int base_mental_threshhold = 0;
private int base_spiritual_threshhold = 0;
private int current_physical_threshhold = 0;
private int current_mental_threshhold = 0;
private int current_spiritual_threshhold = 0;

//Personality
private int base_empathy = 50;
private int base_discipline = 50;
private int base_charity = 50;
private int base_chastity = 50;
private int base_harmony = 50;
private int base_humility = 50;
private int base_industry = 50;
private int base_serenity = 50;
private int base_temperance = 50;
private int base_bravery = 50;
private int base_kindness = 50;
private int base_contentment = 50;
private int current_empathy = 50;
private int current_discipline = 50;
private int current_charity = 50;
private int current_chastity = 50;
private int current_harmony = 50;
private int current_humility = 50;
private int current_industry = 50;
private int current_serenity = 50;
private int current_temperance = 50;
private int current_bravery = 50;
private int current_kindness = 50;
private int current_contentment = 50;

//Needs
private int need_food = 0;
private int_need_drink = 0;
private int need_sleep = 0;
private int need_aid = 0;

private int NF = 1;
private int ND = 2;
private int NS = 3;
private int NI = 4;        //need aid - pain & (body - damage)
private int NO = 5;        //need_boost (mind - coersion)
private int NE = 6;        //need_cheer (soul - depression)

//Base Desires
private int desire_food = 0;
private int_desire_drink = 0;
private int desire_sleep = 0;
private int desire_aid = 0;
private int desire_social = 0;
private int desire_entertain = 0;

private int DF = 20;
private int DD = 21;
private int DS = 22;
private int DI = 23;        //desire aid self - pain
private int DO = 24;        //desire_social vs isolation
private int DE = 25;        //desire_entertain vs boredom

//Social Desires to help others
private int DSF = 50;       //share food with other
private int DSD = 51;       //share drink with other
private int DSS = 52;       //sleep with other
private int DSI = 53;       //aid other vs pain & damage
private int DSO = 54;       //boost other vs isol. & corers.
private int DSE = 55;       //cheer other vs bored. & depress.

private int NA = 0;      //No Action
private int action = NA;

//Relations
private HashMap<Creature,Integer> relationMap;

//Close Relations
private Creature mother = null;
private Creature father = null;
private ArrayList<Creature> siblingList = null;
private ArrayList<Creature> childList = null;

public Creature();
public Creature(int strength, int agility, int willpower, int intelligence, int spirit, int charisma, int awareness);

//Statistic methods
public int getBasePhysical();
public int getBaseStrength();
public int getBaseAgility();
public int getBaseMental();
public int getBaseWillpower();
public int getBaseIntelligence();
public int getBaseSpiritual();
public int getBaseSpirit();
public int getBaseCharisma();
public int getBaseAwareness();
public int getCurrentPhysical();
public int getCurrentStrength();
public int getCurrentAgility();
public int getCurrentMental();
public int getCurrentWillpower();
public int getCurrentIntelligence();
public int getCurrentSpiritual();
public int getCurrentSpirit();
public int getCurrentCharisma();
public int getCurrentAwareness();
public void adjustBasePhysical(int value);
public void adjustBaseStrength(int value);
public void adjustBaseAgility(int value);
public void adjustBaseMental(int value);
public void adjustBaseWillpower(int value);
public void adjustBaseIntelligence(int value);
public void adjustBaseSpiritual(int value);
public void adjustBaseSpirit(int value);
public void adjustBaseCharisma(int value);
public void adjustBaseAwareness(int value);
public void adjustCurrentPhysical(int value);
public void adjustCurrentStrength(int value);
public void adjustCurrentAgility(int value);
public void adjustCurrentMental(int value);
public void adjustCurrentWillpower(int value);
public void adjustCurrentIntelligence(int value);
public void adjustCurrentSpiritual(int value);
public void adjustCurrentSpirit(int value);
public void adjustCurrentCharisma(int value);
public void adjustCurrentAwareness(int value);

//Base pool and threshhold methods
public int getMaxBodt();
public int getMaxMind();
public int getMaxSoul();
public int getCurrentBody();
public int getCurrentMind();
public int getCurrentSoul();
public int getBasePhysicalThreshhold();
public int getBaseMentalThreshhold();
public int getBaseSpritualThreshhold();
public int getCurrentPhysicalThreshhold();
public int getCurrentMentalThreshhold();
public int getCurrentSpritualThreshhold();
public void adjustMaxBody(int value);
public void adjustMaxMind(int value);
public void adjustMaxSoul(int value);
public void adjustCurrentBody(int value);
public void adjustCurrentMind(int value);
public void adjustCurrentSoul(int value);
public void adjustBasePhysicalThreshhold(int value);
public void adjustBaseMentalThreshhold(int value);
public void adjustBaseSpritualThreshhold(int value);
public void adjustCurrentPhysicalThreshhold(int value);
public void adjustCurrentMentalThreshhold(int value);
public void adjustCurrentSpritualThreshhold(int value);

//Personality methods
public void setPersonality(int empathy, int discipline, int charity, int chastity, int harmony, int humility, int industry, int serenity, int temperance);
public int getBaseEmpathy();
public int getBaseDiscipline();
public int getBaseCharity();
public int getBaseChastity();
public int getBaseHarmony();
public int getBaseHumility();
public int getBaseIndustry();
public int getBaseSerenity();
public int getBaseTemperance();
public int getBaseBravery();
public int getBaseKindness();
public int getBaseContentment();
public int getCurrentEmpathy();
public int getCurrentDiscipline();
public int getCurrentCharity();
public int getCurrentChastity();
public int getCurrentHarmony();
public int getCurrentHumility();
public int getCurrentIndustry();
public int getCurrentSerenity();
public int getCurrentTemperance();
public int getCurrentBravery();
public int getCurrentKindness();
public int getCurrentContentment();
public void adjustBaseEmpathy(int value);
public void adjustBaseDiscipline(int value);
public void adjustBaseCharity(int value);
public void adjustBaseChastity(int value);
public void adjustBaseHarmony(int value);
public void adjustBaseHumility(int value);
public void adjustBaseIndustry(int value);
public void adjustBaseSerenity(int value);
public void adjustBaseTemperance(int value);
public void adjustBaseBravery(int value);
public void adjustBaseKindness(int value);
public void adjustBaseContentment(int value);
public void adjustCurrentEmpathy(int value);
public void adjustCurrentDiscipline(int value);
public void adjustCurrentCharity(int value);
public void adjustCurrentChastity(int value);
public void adjustCurrentHarmony(int value);
public void adjustCurrentHumility(int value);
public void adjustCurrentIndustry(int value);
public void adjustCurrentSerenity(int value);
public void adjustCurrentTemperance(int value);
public void adjustCurrentBravery(int value);
public void adjustCurrentKindness(int value);
public void adjustCurrentContentment(int value);

//Needs and Desires
public int getCurrentAction();
public int getNeedFood();
public int getNeedDrink();
public int getNeedSleep();
public int getNeedAid();
public int getNeedHeal();               //Body Damage
public int getNeedBoost();              //Mind Coersion
public int getNeedCheer();              //Soul Depression
public int getDesireFood();
public int getDesireDrink();
public int getDesireSleep();
public int getDesireAid();
public int getDesireSocial();           //vs Isolation
public int getDesireEntertain();        //vs Boredom
public void setCurrentAction(int action);
public void adjustHunger(int value);
public void adjustThirst(int value);
public void adjustSleepiness(int value);
public void adjustPain(int value);
public void adjustIsolation(int value);
public void adjustBoredom(int value);
public void adjustDamage(int value);     //Body Damage
public void adjustCoersion(int value);   //Mind Coersion
public void adjustDepression(int value); //Soul Depression
protected void checkHunger();
protected void checkThirst();
protected void checkSleepiness();
protected void checkPain();
protected void checkIsolation();
protected void checkBoredom();
protected void checkDamage();            //Body Damage
protected void checkCoersion();          //Mind Coersion
protected void checkDepression();        //Soul Depression

//Actions
public void eat();
public void shareFood();
public void drink();
public void shareDrink();
public void nap();
public void shareNap();
public void sleep();
public void aidSelf();
public void aidOther();
public void boostSelf();
public void boostOther();
public void cheerSelf();
public void cheerOther();
public void social();
public void entertainSelf();
public void entertainOther();
//Finding Actions
public GameObject findFood(ArrayList<GridVector> foodLocations);
public GameObject huntFood(ArrayList<GridVector> huntLocations);
public GameObject findDrink(ArrayList<GridVector> drinkLocations);
public GameObject findBed(ArrayList<GridVector> bedLocations);
public GameObject findNearestOther(Creature other);
public GameObject findFriend(ArrayList<Creature> others);
public GameObject findNeutral(ArrayList<Creature> others);
public GameObject findEnemy(ArrayList<Creature> others);
public boolean follow(Creature leader);
public boolean beckon(Creature follower);
public boolean carryTo(GameObject object, GridVector location);
public boolean travelTo(GridVector location);

//Relations
public Creature getMother();
public Creature getFather();
public ArrayList<Creature> getSiblings(); //Full and half
public ArrayList<Creature> getChildren();
public int getRelation(Creature target);
public void setMother(Creature mother);
public void setFather(Creature father);
public void addChild(Creature child);
public void addSibling(Creature sibling);
public void setRelation(Creature target, int relation);
public void adjustRelation(Creature target, int value);

//Other
public Creature clone();
public String toString();
public boolean equals(Object obj);
public int hashCode();


/** 
  * Base class for all civilised creatures in the game world.
 **/
Person.java (extends Creature)

private static final long serialVersionUID = 131L;

//New Desires (26-49)
//New Social Desires (50-99)

//Goals - temporarily cancelled by strong need or desire,
//or completion
private int GF = 100;
private int GD = 101;
private int GS = 102;
private int GI = 103;    //goal aid self - pain
private int GO = 104;    //goal_social vs isol. & corers. 
private int GE = 105;    //goal_entertain vs bored. & depress.

//Social Goals to help others
private int GSF = 150;    //share food with other
private int GSD = 151;    //share drink with other
private int GSS = 152;    //sleep with other
private int GSI = 153;    //aid other vs pain & damage
private int GSO = 154;    //boost other vs isol. & corers.
private int GSE = 155;    //cheer other vs bored. & depress.

//Simple Goals
private int GWa = 200;    //Wait
private int GEq = 201;    //Equip Self
private int GBe = 202;    //Create Bed
private int GFo = 203;    //Gather Food
private int GDr = 204;    //Gather Drink

//Complex Goals
private int CGHu = 250;    //Hunt
private int CGCo = 251;    //Cook
private int CGTr = 252;    //Trade

//Job
private int job_hunter = 500;
private int job_gatherer = 501;
private int job_cook = 502;
private int job_trader = 503;


private int NA = 0;      //No Action
private int goal = NA;

private int job = NA;


//Goals
public int getCurrentGoal();
public void setCurrentGoal(int goal);

//Job - the base job for this person
public int getJob();
public void setJob(int job);

//Actions
public void wait();
public void equipSelf(ArrayList<GameObject> equipmentList);

