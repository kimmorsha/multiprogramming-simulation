import java.util.*;
import java.io.*;
import java.lang.*;

class Resource {
	int number;
	int time = 0;
	String name;
	String status = "Free"; //FREE or USED

	public Resource(int number, int time) {
		this.number = number;
		this.name = "resource " + number;
		this.time = time;
	}
}

class Waiting {
	int number;
	int time;
	String name;
	int user;

	public Waiting(int number, int time, int user) {
		this.number = number;
		this.time = time;
		this.user = user;
		this.name = "resource " + number;
	}
}

class User {
	int number;
	String name;
	Resource[] resources;
	String status = "."; //USING something, WAITING FOR something, DONE

	public User(int number) {
		this.number = number;
		this.name = "user " + number;
	}
}

class Multiprogramming {
	private static final int PAGE_SIZE = 40;
	public static void main(String[] args) throws InterruptedException{
		Multiprogramming mp = new Multiprogramming();

		int numberOfResources = mp.generateRandomNumber(1,30);
		int numberOfUsers = mp.generateRandomNumber(1,30);
		// System.out.println("numberOfResources = " + numberOfResources);
		// System.out.println("numberOfUsers = " + numberOfUsers);

		Resource[] resources = mp.generateResources(numberOfResources);

		int[] resourceNumbers = mp.getResourceNumbers(resources);
		//mp.printResourceNumberArray(resourceNumbers);

		User[] users = mp.generateUsers(numberOfUsers, resourceNumbers);
		//mp.printUsers(users);

		Arrays.sort(resourceNumbers);
		//mp.printResourceNumberArray(resourceNumbers);

		ArrayList<Waiting> waiting = new ArrayList<Waiting>();
		
		while (mp.isNotYetOver(users)) {
			System.out.println("\n\nUSERS (" + numberOfUsers + ")" + "\t\t\t\t\t\t\t\tRESOURCES (" + numberOfResources + ")");
			for (int i = 0; i < users.length; i++) {
				Resource temp;
				int resourcesIndex = mp.getIndexOfNextResource(users[i].resources);

				if (resourcesIndex < users[i].resources.length) {
					temp = users[i].resources[resourcesIndex];
					Waiting tempWait = new Waiting(temp.number, temp.time, users[i].number);
					if (waiting == null || !mp.isWaiting(waiting, tempWait)) {
						int resIndex = mp.getIndexOfResource(resources, temp.number);
						resources[resIndex].status = "USED";
						users[i].resources[resourcesIndex].status = "USED";
						users[i].resources[resourcesIndex].time--;
						users[i].status = "Using " + temp.name + " (" + temp.time + ")\t";
						if (!mp.isWaitingSameUser(waiting, tempWait)) {
							waiting.add(tempWait);
						}
						if (users[i].resources[resourcesIndex].time == 0) {
							//System.out.println("******11TEST IM REMOVING SOMETHING IN WAITLIST");
							int index = mp.getIndexOfWaiting(waiting, tempWait);
							if (index < waiting.size()) {
								//System.out.println("!!!! index is " + index);
								waiting.remove(index);
								resources[resIndex].status = "FREE";
							} else {
								System.out.println("**** index is -1");
							}
						}
					} else {
						if (users[i].resources[resourcesIndex].time == 0) {
							//System.out.println("******22TEST IM REMOVING SOMETHING IN WAITLIST");
							int index = mp.getIndexOfWaiting(waiting, tempWait);
							if (index < waiting.size()) {	
							//	System.out.println("!!!! index is " + index);
								waiting.remove(index);
							} else {
								//.out.println("**** index is -1");
							}
						}
						users[i].status = "Waiting for " + temp.name + " (" + temp.time + ")";
					}
				} else {
					users[i].status = "Done";
				}
				//mp.printWaiting(waiting);
				System.out.print("\n" + users[i].name + "   |   Status : " + users[i].status);
				if (i < numberOfResources) {
					System.out.print("\t\t\t" + resources[i].name + "   |   Status : " + resources[i].status);
				}
				
			}
			if (numberOfUsers < numberOfResources) {
				for (int i = 0; i < numberOfResources-numberOfUsers; i++) {
					System.out.print("\n\t\t\t\t\t\t\t\t\t" + resources[i].name + "   |   Status : " + resources[i].status);
				}
			}
			//mp.clearScreen();
			Thread.sleep(1500);

		}
	}

	boolean isNotYetOver(User[] users) {
		for (int i = 0; i < users.length; i++) {
			int size = users[i].resources.length-1;
			if (users[i].resources[size].time > 0) {
				return true;
			}
		}
		return false;
	}

	int getIndexOfNextResource(Resource[] resources) {
		int i = 0;
		for (; i < resources.length; i++) {
			if (resources[i].time > 0) {
				return i;
			}
		}
		return i;
	}

	int getIndexOfResource(Resource[] resources, int number) {
		int i = 0;
		for (; i < resources.length; i++) {
			if (resources[i].number == number) {
				return i;
			}
		}
		return i;
	}

	int getIndexOfWaiting(ArrayList<Waiting> waiting, Waiting wait) {
		int i = 0;
		for (; i < waiting.size(); i++) {
			Waiting temp = waiting.get(i);
			if (temp.number == wait.number && temp.user == wait.user) {
				return i;
			}
		}
		return i;
	}

	boolean isWaiting(ArrayList<Waiting> waiting, Waiting wait) {
		for (int i = 0; i < waiting.size(); i++) {
			Waiting temp = waiting.get(i);
			if (temp.number == wait.number) {
				if (temp.user == wait.user) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	boolean isWaitingSameUser(ArrayList<Waiting> waiting, Waiting wait) {
		for (int i = 0; i < waiting.size(); i++) {
			Waiting temp = waiting.get(i);
			if (temp.number == wait.number) {
				if (temp.user == wait.user) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	Resource[] generateResources(int numberOfResources) {
		Resource[] resources = new Resource[numberOfResources];
		ArrayList<Integer> usedResources = new ArrayList<Integer>();
		int number;
		for (int i = 0; i < numberOfResources; i++) {
			number = this.generateRandomNumber(1,30);
			while (usedResources.contains(number)) {
				number = this.generateRandomNumber(1,30);
			}
			usedResources.add(number);
			resources[i] = new Resource(number, 0);
		}
		return resources;
	}

	User[] generateUsers(int numberOfUsers, int[] resourceNumbers) {
		User[] users = new User[numberOfUsers];
		int numOfResource = this.generateRandomNumber(1, resourceNumbers.length);
		for (int i = 0; i < numberOfUsers; i++) {
			users[i] = new User(i+1);
			int num = this.generateRandomNumber(1, numOfResource);
			Resource[] resource = new Resource[num];
			ArrayList<Integer> usedResources = new ArrayList<Integer>();
			for (int j = 0; j < num; j++) {
				int index = this.generateRandomNumber(1, numOfResource);
				while (usedResources.contains(index)) {
					index = this.generateRandomNumber(1, numOfResource);
				}
				usedResources.add(index);
				int time = this.generateRandomNumber(1, 30);
				int number = resourceNumbers[index-1];
				resource[j] = new Resource(number, time); 
			}
			users[i].resources = resource;
		}
		return users;
	}

	public int[] getResourceNumbers(Resource[] resources) {
		int [] resourceNumbers = new int[resources.length];
		for (int i = 0 ; i < resources.length; i++) {
			resourceNumbers[i] = resources[i].number;
		}
		return resourceNumbers;
	}

	public int generateRandomNumber(int min, int max){
		Random randomGenerator = new Random();
		return randomGenerator.nextInt((max - min) + 1) + min;
	}

	public void printResources(Resource[] resources) {
		System.out.println("RESOURCES (" + resources.length + ")");
		for (int i = 0; i < resources.length; i++) {
			System.out.println(resources[i].name + " (" + resources[i].time + ")");
		}
	}

	public void printResourceNumberArray(int[] resourceNumbers) {
		System.out.println("RESOURCE NUMBERS (" + resourceNumbers.length + ")");
		System.out.println("[");
		for (int i = 0; i < resourceNumbers.length; i++) {
			System.out.print(" [" + resourceNumbers[i] + "]");
		}
		System.out.println("]");
	}
	public void printUsers(User[] users) {
		System.out.println("USERS (" + users.length + ")");
		for (int i = 0; i < users.length; i++) {
			System.out.print(users[i].name);
			System.out.print(" resource for " + users[i].name + " = ");
			this.printResources(users[i].resources);
		}
	}

	public void printWaiting(ArrayList<Waiting> waiting) {
		System.out.println("WAITNG LIST");
		for (int i = 0; i < waiting.size(); i++) {
			Waiting temp = waiting.get(i);
			System.out.println(" ["+temp.name+","+temp.user+"]");
		}
	}

	private static void clearScreen() {
	    for (int i = 0; i < PAGE_SIZE; i++) {
	        System.out.println();
	    }
	}
}