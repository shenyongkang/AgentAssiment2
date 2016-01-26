package pacman.entries.ghosts;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public final class MyGhosts extends Controller<EnumMap<GHOST,MOVE>>
{	
	private final static float CONSISTENCY=0.95f;	//attack Ms Pac-Man with this probability
	private final static int PILL_PROXIMITY=15;		//if Ms Pac-Man is this close to a power pill, back away
	
	Random rnd=new Random();
	EnumMap<GHOST,MOVE> myMoves=new EnumMap<GHOST,MOVE>(GHOST.class);
	
	public EnumMap<GHOST,MOVE> getMove(Game game,long timeDue)
	{
		for(GHOST ghost : GHOST.values())	//for each ghost
		{			
			DM mode = DM.PATH;
			if (ghost == GHOST.BLINKY){
				mode = DM.EUCLID;
				
			}else if(ghost == GHOST.INKY){
				mode = DM.MANHATTAN;
				
			}else if (ghost == GHOST.PINKY){
				mode = DM.PATH;
			}else{
				
			}
			if(game.doesGhostRequireAction(ghost))		//if ghost requires an action
			{
				if(game.getGhostEdibleTime(ghost)>0 || closeToPower(game))	//retreat from Ms Pac-Man if edible or if Ms Pac-Man is close to power pill
					myMoves.put(ghost,game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
							game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(ghost),mode));
				else 
				{
					if(rnd.nextFloat()<CONSISTENCY)			//attack Ms Pac-Man otherwise (with certain probability)
						myMoves.put(ghost,game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
								game.getPacmanCurrentNodeIndex(),game.getGhostLastMoveMade(ghost),mode));
					else									//else take a random legal action (to be less predictable)
					{					
						MOVE[] possibleMoves=game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost),game.getGhostLastMoveMade(ghost));
						myMoves.put(ghost,possibleMoves[rnd.nextInt(possibleMoves.length)]);
					}
				}
			}
		}

		return myMoves;
	}
	
    //This helper function checks if Ms Pac-Man is close to an available power pill
	private boolean closeToPower(Game game)
    {
    	int[] powerPills=game.getPowerPillIndices();
    	
    	for(int i=0;i<powerPills.length;i++)
    		if(game.isPowerPillStillAvailable(i) && game.getShortestPathDistance(powerPills[i],game.getPacmanCurrentNodeIndex())<PILL_PROXIMITY)
    			return true;

        return false;
    }
}