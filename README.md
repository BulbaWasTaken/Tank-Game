# csc413-tankgame


| Student Information |                        |
|:-------------------:|------------------------|
|  Student Name       |   Karl Xavier Layco    |
|  Student Email      |   klayco1@sfsu.edu     |

## src Folder Purpose 
src folder is to be used to store source code only.

## resources Folder Purpose 
resources folder is to be used to store the resources for your project only. This includes images, sounds, map text files, etc.

`The src and resources folders can be deleted if you want a different file structure`

## jar Folder Purpose 
The jar folder is to be used to store the built jar of your term-project.

`NO SOURCE CODE SHOULD BE IN THIS FOLDER. DOING SO WILL CAUSE POINTS TO BE DEDUCTED`

`THIS FOLDER CAN NOT BE DELETED OR MOVED`

# Required Information when Submitting Tank Game

## Version of Java Used: 
    Java 20.0.1

## IDE used: 
    IntelliJ IDEA

## Steps to Import project into IDE:
    1.) Import from repository. Put the cloned folder in to your desired directory.
    2.) Use in console: git clone *HTTPS or SSH Key* *desired folder name*
    3.) Open IDE of your choice
    4.) Make a new project
    5.) Import cloned folder
            or
    Download .jar file in the jar folder from the repository.

## Steps to Build your Project:
    Make sure Java is installed in your device.
    Using IntelliJ:
        Make sure the root folder when importing is the cloned folder
        Then go to File > Project Structure > Modules
        Find the resource folder then mark it as Resources

        To build the .jar:
            Project Structure > Artifacts 
            Select the folder
            Hit APPLY then OK
            Go to BUILD > BUILD ARTIFACTS > Select the folder
            Then click build

 
## Steps to run your Project:
    Using the IDE: Run the java class: Launcher.java
            or
    In console, run the command:
        java -jar .\CSC413-TermProject 

## Controls to play your Game:

|               | Player 1 | Player 2 |
|---------------|----------|----------|
|  Forward      |    W     |    ↑     |
|  Backward     |    S     |    ↓     |
|  Rotate left  |    A     |    ←     |
|  Rotate Right |    D     |    →     |
|  Shoot        |  SPACE   |  Num_0   |

<!-- you may add more controls if you need to. -->