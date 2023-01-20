import extensions.CSVFile;
import extensions.File;

class SchoolLegacy extends Program {

//CONSTANTES

    final String CHEMIN_MATIERES = "../ressources/matieres/";
    final String CHEMIN_MAPS = "../ressources/maps/";
    final String CHEMIN_SAUVEGARDE = "../ressources/sauvegarde/";
    final int TEMPS_DELAI = 20;
    final int NB_SALLE = 5;

    //Variable globale pour l'affichage des salles
    String COULEUR_HERO = ANSI_GREEN;
    String COULEUR_PORTE_FERMEE = ANSI_RED;
    String COULEUR_PORTE_OUVERTE = ANSI_GREEN;
    String COULEUR_PNJ = ANSI_BLUE;

//

//BOUCLE PRINCIPALE

    void algorithm() {
        clearScreen();
        hide();
        printlnAvecDelai(ANSI_BOLD + "Bienvenue dans " + ANSI_BLUE + "School Legacy" + ANSI_RESET + ANSI_BOLD + ", le jeu qui teste vos connaissances.\n");
        printlnAvecDelai("1 - Nouvelle partie");
        printlnAvecDelai("2 - Charger une partie");
        printlnAvecDelai("3 - Quitter\n");
        show();
        int choice = readBetterInt();
        hide();
        Joueur[] joueurs = listerJoueurs();
        Joueur joueur = newJoueur("???");
        while (choice > 3 || choice < 1) {
            println("Veuillez entrer un nombre entre 1 et 3.");
            show();
            choice = readBetterInt();
            hide();
        }
        if (choice == 1) {
            newGame(joueur, length(joueurs));
        } else if (choice == 2) {
            println(scoreBoard(joueurs));
            show();
            int partie = readBetterInt();
            hide();
            joueur = joueurs[partie - 1];
            joueur.matieresChoisie = initMatieres(joueur.matieresChoisie);
            game(joueurs[partie - 1], partie-1);
        } else {
            printlnAvecDelai("Merci d'avoir joué à School Legacy !" + ANSI_RESET);
        }
        show();
    }

    void newGame(Joueur j, int numJoueur) {
        clearScreen();
        printlnAvecDelai("... Enfin, j'ai bien cru que vous dormiriez pour toujours !");
        printlnAvecDelai("Vous vous trouvez actuellement au fond du School Dungeon.");
        printlnAvecDelai("Pour sortir de ce lieu, vous devrez répondre à une série de question.");
        printlnAvecDelai("Avant de partir laissez moi vous expliquer quelques petites choses.");
        printlnAvecDelai("Vous pouvez utiliser les commandes suivantes :");
        printlnAvecDelai("z - Monter");
        printlnAvecDelai("s - Descendre");
        printlnAvecDelai("q - Aller à gauche");
        printlnAvecDelai("d - Aller à droite");
        printlnAvecDelai("e - Interagir");
        printlnAvecDelai("t - Sauvegarder");
        printlnAvecDelai("x - Quitter le jeu");
        printlnAvecDelai("Maintenant, passez cette porte.");
        printlnAvecDelai("... Attendez ! J'ai oublié de vous demandez, quel est votre nom ?" + ANSI_RED);
        show();
        j = newJoueur(readString());
        hide();
        print(ANSI_RESET + ANSI_BOLD);
        delay(300);
        clearScreen();
        printlnAvecDelai("Veuillez aussi choisir les matières que vous souhaitez étudier durant votre exploration");
        choixMatiere(j);
        printlnAvecDelai("Bonne chance " + j.pseudo + " !");
        delay(1000);
        clearScreen();
        game(j, numJoueur);
    } 

    void game(Joueur j, int numJoueur) {
        Entites[][] salle = genSalle(CHEMIN_MAPS + "salleFermee.csv");
        int nbEtage = length(j.matieresChoisie);
        Matiere matiereEtage = choisirMatiere(j);
        Question questionSalle;
        while (true) {
            println("Rappel commande : z - Monter, s - Descendre, q - Aller à gauche, d - Aller à droite, e - Interagir, t - Sauvegarder, x - Quitter le jeu");
            println(toString(j));
            afficherSalle(salle, j);
            println("Vous êtes actuellement dans l'étage \"" + ANSI_BLUE + matiereEtage.nom + ANSI_RESET + ANSI_BOLD + "\".");
            print("Commande : ");
            show();
            int executerCommand = executerCommand(readString(), salle, j);
            hide();
            if (executerCommand == 0) {
                questionSalle = choisirQuestion(matiereEtage);
                println();
                printlnAvecDelai("Questions de difficulté " + difficulteToString(questionSalle.difficulte) + " :");
                printlnAvecDelai(questionSalle.intitule);
                printAvecDelai("Réponse : ");
                show();
                if (equals(questionSalle.reponse, toLowerCase(readString()))) {
                    hide();
                    println();
                    printlnAvecDelai("Bonne réponse.\nJ'ajoute " + questionSalle.difficulte + " points à votre score.");
                    j.score += questionSalle.difficulte;
                    questionSalle.fini = true;
                    printlnAvecDelai("Une autre salle vous attend.");
                    delay(700);
                    salle = genSalle(CHEMIN_MAPS + "salleOuverte.csv");
                } else {
                    hide();
                    println();
                    printlnAvecDelai("Mauvaise réponse\nVous ne gagnez aucun point.");
                    printlnAvecDelai("Merci de réessayer.");
                    delay(700);
                }
            } else if (executerCommand == 1) {
                salle = changementSalle(salle, j);
                if (j.salleActuelle == NB_SALLE + 1) {
                    j.etageActuel++;
                    j.salleActuelle = 1;
                    if (j.etageActuel == nbEtage) {
                        println("Bravo vous êtes sortie du donjon !\nVotre score est de " + j.score + " points.");
                        break;
                    }
                    salle = genSalle(CHEMIN_MAPS + "salleFermee.csv");
                    matiereEtage.fini = true;
                    matiereEtage = choisirMatiere(j);
                    printlnAvecDelai("Vous passez à l'étage suivant.\nCette fois-ci, vous allez devoir répondre à des questions en \"" + ANSI_BLUE + matiereEtage.nom + ANSI_RESET + ANSI_BOLD + "\".");
                    delay(700);
                } else {
                    salle = genSalle(CHEMIN_MAPS + "salleFermee.csv");
                    printlnAvecDelai("La porte se referme derrière vous.");
                }
            } else if (executerCommand == 2) {
                printlnAvecDelai("Voulez-vous sauvegarder la partie ? (O/N)");
                if (charAt(toUpperCase(readString() + " "), 0) == 'O') {
                    sauvegarderJoueur(j, numJoueur);
                    printlnAvecDelai("Partie sauvegardée.");
                    delay(700);
                }
            } else if (executerCommand == 3) {
                printlnAvecDelai("Vous avez quitté le jeu.");
                break;
            } else if (executerCommand == 3) {
                break;
            }
            clearScreen();
        }
        printlnAvecDelai("Merci d'avoir joué !" + ANSI_RESET);
    }

//

//JOUEUR

    Joueur newJoueur(String pseudo) {
        Joueur j = new Joueur();
        if (length(pseudo) > 0) {
            j.pseudo = pseudo;
        }
        return j;
    }

    String toString(Joueur j) {
        return ANSI_GREEN + j.pseudo + ANSI_RESET + ANSI_BOLD + " (" + j.score + " points) est actuellement en " + ANSI_RED + j.positionLigne + ANSI_RESET + ANSI_BOLD + ", " + ANSI_RED + j.positionColonne + ANSI_RESET + ANSI_BOLD + " dans la salle " + ANSI_RED + j.salleActuelle + ANSI_RESET + ANSI_BOLD + " de l'étage " + ANSI_RED + j.etageActuel + ANSI_RESET + ANSI_BOLD + ".";
    }

//

//QUESTION
    
    //Création d'une question
    Question newQuestion(int difficulte, String intitule, String reponse) {
        Question q = new Question();
        q.difficulte = difficulte;
        q.intitule = intitule;
        q.reponse = reponse;
        return q;
    }

    //Retourne une string contenant la difficulté de la question, son intitulé et sa réponse
    String toString(Question q) {
        return "Question de difficulté " + q.difficulte + " : " + q.intitule + " (" + q.reponse + ")";
    }

    //Liste les questions d'une matière
    Question[] listerQuestions(String nomMatiere) {
        String chemin = CHEMIN_MATIERES + nomMatiere + ".csv";
        CSVFile fichier = loadCSV(chemin, ';');
        Question[] questions = new Question[rowCount(fichier)-1];
        for (int idx = 1; idx <= length(questions); idx++) {
            questions[idx-1] = newQuestion(stringToInt(getCell(fichier, idx, 0)), getCell(fichier, idx, 1), getCell(fichier, idx, 2));
        }
        return questions;
    }

    //Compte les questions par difficulté
    int[] compterQuestions(Question[] questions) {
        int[] compteur = new int[3];
        for (int idx = 0; idx < length(questions); idx++) {
            compteur[questions[idx].difficulte-1]++;
        }
        return compteur;
    }
    
    //Trie les questions par difficulté
    Question[][] trierQuestions(Question[] questions) {
        Question[][] questionsTrie = new Question[3][];
        int[] compteur = compterQuestions(questions);
        int questionDejaTriee = 0;
        for (int idx = 0; idx < 3 ; idx++) {
            questionsTrie[idx] = new Question[compteur[idx]];
            for (int idx2 = 0; idx2 < compteur[idx]; idx2++) {
                questionsTrie[idx][idx2] = questions[questionDejaTriee + idx2];
            }
            questionDejaTriee += compteur[idx];
        }
        return questionsTrie;
    }

    //Initialise toute les questions d'un tableau de matières
    void initQuestions(Matiere[] matieres) {
        for (int idx = 0; idx < length(matieres); idx++) {
            if (matieres[idx].fini == false) {
                matieres[idx].questions = trierQuestions(listerQuestions(matieres[idx].nom));
            }
        }
    }

    //Vérifie si toute les questions d'une difficulté ont été résolue
    boolean difficulteFinie(Matiere m, int difficulte) {
        for (int idx = 0; idx < length(m.questions[difficulte]); idx++) {
            if (m.questions[difficulte][idx].fini == false) {
                return false;
            }
        }
        return true;
    }

    //Choix aléatoire d'une question parmis les questions non résolue d'une matiere
    Question choisirQuestion(Matiere m) {
        int difficulte = (int) (random() * length(m.questions));
        while (difficulteFinie(m, difficulte)) {
            difficulte = (int) (random() * length(m.questions));
        }
        int idx = (int) (random() * length(m.questions[difficulte]));
        while (m.questions[difficulte][idx].fini) {
            idx = (int) (random() * length(m.questions[difficulte]));
        }
        return m.questions[difficulte][idx];
    }

//

//MATIERE

    //Créer une matière
    Matiere newMatiere(String nom) {
        Matiere m = new Matiere();
        m.nom = nom;
        return m;
    }

    //Renvoie une string illustrant une matière
    String toString(Matiere m) {
        String s = "Matière " + m.nom + " :\n";
        for (int idx = 0; idx < length(m.questions); idx++) {
            for (int idx2 = 0; idx2 < length(m.questions[idx]); idx2++) {
                s += toString(m.questions[idx][idx2]) + "\n";
            }
        }
        return s;
    }

    //Liste les matières disponibles dans le dossier matières
    Matiere[] listerMatieres() {
        String[] listeMatiere = getAllFilesFromDirectory(CHEMIN_MATIERES);
        Matiere[] matieres = new Matiere[length(listeMatiere)];
        for (int idx = 0; idx < length(listeMatiere); idx++) {
            matieres[idx] = newMatiere(substring(listeMatiere[idx], 0, length(listeMatiere[idx])-4));
        }
        return matieres;
    }

    //Initialise un tableau de matières
    Matiere[] initMatieres(Matiere[] matieres) {
        initQuestions(matieres);
        return matieres;
    }

    //Initialise les matières que le joueur souhaite étudier
    void choixMatiere(Joueur j) {
        Matiere[] matieres = listerMatieres();
        boolean[] booleanMatiere = new boolean[length(matieres)];
        int nbChoix = 0;
        int choix;
        int idx2 = 0;
        do {
            if (nbChoix < 1) {
                println("Vous devez choisir au moins une matière !");
            }
            println("Matiére disponible (" + ANSI_RED + "Rouge" + ANSI_RESET + ANSI_BOLD + " = Choisi, " + ANSI_BLUE + "Bleu" + ANSI_RESET + ANSI_BOLD + " = Non Choisi):");
            println("Rentrer le numéro de la matière pour la choisir ou la déchoisir, ou 0 pour valider");
            for (int idx = 0; idx < length(matieres); idx++) {
                if (booleanMatiere[idx]) {
                    print(ANSI_RED + matieres[idx].nom + " (" + (idx+1) + ")" + ANSI_RESET + ANSI_BOLD + ", ");
                } else {
                    print(ANSI_BLUE + matieres[idx].nom + " (" + (idx+1) + ")" + ANSI_RESET + ANSI_BOLD + ", ");
                }
            }
            println(); 
            show();
            choix = readBetterInt();
            hide();
            if (choix > 0 && choix <= length(matieres)) {
                if (!booleanMatiere[choix-1]) {
                    booleanMatiere[choix-1] = true;
                    nbChoix++;
                } else {
                    booleanMatiere[choix-1] = false;
                    nbChoix--;
                }
            }
            clearScreen();
        } while (choix != 0 || nbChoix < 1);
        j.matieresChoisie = new Matiere[nbChoix];
        for (int idx = 0; idx < length(matieres); idx++) {
            if (booleanMatiere[idx]) {
                j.matieresChoisie[idx2] = matieres[idx];
                idx2++;
            }
        }
        initMatieres(j.matieresChoisie);
    }

    //Choix aléatoire d'une matière parmis les matières non finies
    Matiere choisirMatiere(Joueur j) {
        do {
            int idx = (int) (random() * length(j.matieresChoisie));
            if (!j.matieresChoisie[idx].fini) {
                return j.matieresChoisie[idx];
            }
        } while (true);
    }

// 

//MAP
    
    //Créer une salle à partir d'un fichier csv donnée en paramètres
    Entites[][] genSalle(String s) {
        CSVFile fichier = loadCSV(s);
        Entites[][] salle = new Entites[rowCount(fichier)-1][columnCount(fichier)];
        int[] positionPNJ = positionPNJRandom(salle);
        boolean pnjPresent = false;
        for (int idxL = 1; idxL < rowCount(fichier); idxL++) {
            for (int idxC = 0; idxC < columnCount(fichier); idxC++) {
                if (equals(getCell(fichier, idxL, idxC), "*")) {
                    salle[idxL-1][idxC] = Entites.BORDURE;
                } else if (equals(getCell(fichier, idxL, idxC), "V")) {
                    salle[idxL-1][idxC] = Entites.VIDE;
                } else if (equals(getCell(fichier, idxL, idxC), "P")) {
                    salle[idxL-1][idxC] = Entites.PORTE_FERMEE;
                } else if (equals(getCell(fichier, idxL, idxC), "p")) {
                    salle[idxL-1][idxC] = Entites.PORTE_OUVERTE;
                } else if (equals(getCell(fichier, idxL, idxC), "O")) {
                    pnjPresent = true;
                    salle[idxL-1][idxC] = Entites.VIDE;
                }
            }
        }
        if (pnjPresent) {
            salle[positionPNJ[0]][positionPNJ[1]] = Entites.PNJ;
        }
        return salle;
    }

    //Choisi un type de bordure en fonction de sa position dans la salle
    char typeBordure(Entites[][] salle, int idxL, int idxC) {
        if (idxL == 0 && idxC == 0) {
            return '▛';
        } else if (idxL == 0 && idxC == length(salle, 2) - 1) {
            return '▜';
        } else if (idxL == length(salle, 1) - 1 && idxC == 0) {
            return '▙';
        } else if (idxL == length(salle, 1) - 1 && idxC == length(salle, 2) - 1) {
            return '▟';
        } else if (idxL == 0) {
            return '▀';
        } else if (idxC == 0) {
            return '▌';
        } else if (idxC == length(salle, 2) - 1) {
            return '▐';
        } else if (idxL == length(salle, 1) - 1) {
            return '▄';
        }
        return ' ';
    }

    //Choisi un type de porte en fonction de sa position dans la salle
    char typePorte(Entites[][] salle, int idxL, int idxC) {
        if (idxL == 0) {
            return '▀';
        } else if (idxC == 0) {
            return '▌';
        } else if (idxC == length(salle, 2) - 1) {
            return '▐';
        } else if (idxL == length(salle, 1) - 1) {
            return '▄';
        }
        return ' ';
    }

    //Affiche la salle dans la console
    void afficherSalle(Entites[][] salle, Joueur joueur) {
        for (int idxL = 0; idxL < length(salle, 1); ++idxL) {
            for (int idxC = 0; idxC < length(salle, 2); ++idxC) {
                if (salle[idxL][idxC] == Entites.BORDURE) {
                    print(ANSI_RESET + ANSI_BOLD + typeBordure(salle, idxL, idxC));
                } else if (salle[idxL][idxC] == Entites.PNJ) {
                    print(COULEUR_PNJ + "!");
                } else if (salle[idxL][idxC] == Entites.PORTE_FERMEE) {
                    print(COULEUR_PORTE_FERMEE + typePorte(salle, idxL, idxC));
                } else if (salle[idxL][idxC] == Entites.PORTE_OUVERTE) {
                    print(COULEUR_PORTE_OUVERTE + typePorte(salle, idxL, idxC));
                } else if (joueur.positionLigne == idxL && joueur.positionColonne == idxC) {
                    print(COULEUR_HERO + charAt(joueur.pseudo + "?", 0));
                } else {
                    print(" ");
                }
            }
            println();
        }
    }

    //Génére une nouvelle salle, reinitialise la position du joueur et renvoie la nouvelle salle
    Entites[][] changementSalle(Entites[][] genSalle, Joueur joueur) {
        Entites[][]salle = genSalle(CHEMIN_MAPS + "salleFermee.csv");
        joueur.positionLigne = length(salle) / 2;
        joueur.positionColonne = 1;
        joueur.salleActuelle++;
        return salle;
    }

    int[] positionPNJRandom (Entites[][] salle) {
        int[] positionPNJ = new int[2];
        positionPNJ[0] = (int) (random() * (length(salle, 1) - 2)) + 1;
        positionPNJ[1] = (int) (random() * (length(salle, 2) - 2)) + 1;
        return positionPNJ;
    }

//

//DEPLACEMENT ET ACTION
    
    //Execute la commande donnée en paramètre et renvoie un integer correspondant à l'action effectuée
    int executerCommand(String s, Entites[][] salle, Joueur joueur) {
        char command = charAt(toUpperCase(s + " "), 0);
        int interaction;
        if (command == 'Z') {
            if (!deplacer(salle, joueur, 'N')) {
                println("Vous ne pouvez pas vous déplacer par là");
            }
        } else if (command == 'S') {
            if (!deplacer(salle, joueur, 'S')) {
                println("Vous ne pouvez pas vous déplacer par là");
            }
        } else if (command == 'D') {
            if (!deplacer(salle, joueur, 'E')) {
                println("Vous ne pouvez pas vous déplacer par là");
            }
        } else if (command == 'Q') {
            if (!deplacer(salle, joueur, 'O')) {
                println("Vous ne pouvez pas vous déplacer par là");
            }
        } else if (command == 'E') {
            interaction = interagir(salle, joueur);
            if (interaction == -1) {
                println("Vous ne pouvez pas intéragir avec ça");
            }
            return interaction;
        } else if (command == 'T') {
            return 2;
        } else if (command == 'X') {
            return 3;
        } else {
            println("Commande inconnue");
        }
        return -1;
    }
    
    //Déplace le joueur dans la direction donnée en paramètre et renvoie un boolean indiquant si le déplacement a été effectué
    boolean deplacer(Entites[][] salle, Joueur joueur, char direction) {
        if (salle[joueur.positionLigne - 1][joueur.positionColonne] == Entites.VIDE && direction == 'N') {
            joueur.positionLigne--;
            joueur.direction = direction;
            return true;
        } else if (salle[joueur.positionLigne + 1][joueur.positionColonne] == Entites.VIDE && direction == 'S') {
            joueur.positionLigne++;
            joueur.direction = direction;
            return true;
        } else if (salle[joueur.positionLigne][joueur.positionColonne + 1] == Entites.VIDE && direction == 'E') {
            joueur.positionColonne++;
            joueur.direction = direction;
            return true;
        } else if (salle[joueur.positionLigne][joueur.positionColonne - 1] == Entites.VIDE && direction == 'O') {
            joueur.positionColonne--;
            joueur.direction = direction;
            return true;
        }
        joueur.direction = direction;
        return false;
    }
    
    //Renvoie un integer selon l'entité avec laquelle le joueur intéragit
    int interagir(Entites[][] salle, Joueur joueur) {
        if (joueur.direction == 'N') {
            if (salle[joueur.positionLigne - 1][joueur.positionColonne] == Entites.PNJ) {
                return 0;
            } else if (salle[joueur.positionLigne - 1][joueur.positionColonne] == Entites.PORTE_OUVERTE) {
                return 1;
            }
        } else if (joueur.direction == 'S') {
            if (salle[joueur.positionLigne + 1][joueur.positionColonne] == Entites.PNJ) {
                return 0;
            } else if (salle[joueur.positionLigne + 1][joueur.positionColonne] == Entites.PORTE_OUVERTE) {
                return 1;
            }
        } else if (joueur.direction == 'E') {
            if (salle[joueur.positionLigne][joueur.positionColonne + 1] == Entites.PNJ) {
                return 0;
            } else if (salle[joueur.positionLigne][joueur.positionColonne + 1] == Entites.PORTE_OUVERTE) {
                return 1;
            }
        } else if (joueur.direction == 'O') {
            if (salle[joueur.positionLigne][joueur.positionColonne - 1] == Entites.PNJ) {
                return 0;
            } else if (salle[joueur.positionLigne][joueur.positionColonne - 1] == Entites.PORTE_OUVERTE) {
                return 1;
            }
        }
        return -1;
    }

//

//MINASCELLANEOUS

    boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    int readBetterInt() {
        String input = readString();
        int idx = 0;
        while (idx < length(input) && !isDigit(charAt(input, idx))) {
            println("Veuillez entrer un nombre.");
            input = readString();
        }
        return stringToInt(input);
    }

    void printAvecDelai(String s) {
        for (int idx = 0; idx < length(s); idx++) {
            delay(TEMPS_DELAI);
            print(charAt(s, idx));
        }
    }

    void printlnAvecDelai(String s) {
        printAvecDelai(s);
        println();
    }

    //Transforme le numéro de la difficulté sous forme d'une chaine d'étoiles
    String difficulteToString(int difficulté) {
        if (difficulté == 1) {
            return "*";
        } else if (difficulté == 2) {
            return "**";
        } else {
            return "***";
        }
    }

    String booleanToString(boolean b) {
        if (b) {
            return "true";
        } 
        return "false";
    }

//

//SAUVEGARDE 

    Joueur[] listerJoueurs() {
        CSVFile fichierJoueurs = loadCSV(CHEMIN_SAUVEGARDE + "joueurs.csv");
        Joueur[] joueurs = new Joueur[0];
        Matiere[] matieres;
        int nbChoix = 0;
        if (rowCount(fichierJoueurs) > 1) {
            joueurs = new Joueur[rowCount(fichierJoueurs) - 1];
            for (int idx = 1; idx < rowCount(fichierJoueurs); idx++) {
                joueurs[idx - 1] = new Joueur();
                joueurs[idx - 1].pseudo = getCell(fichierJoueurs, idx, 0);
                joueurs[idx - 1].positionLigne = stringToInt(getCell(fichierJoueurs, idx, 1));
                joueurs[idx - 1].positionColonne = stringToInt(getCell(fichierJoueurs, idx, 2));
                joueurs[idx - 1].salleActuelle = stringToInt(getCell(fichierJoueurs, idx, 3));
                joueurs[idx - 1].etageActuel = stringToInt(getCell(fichierJoueurs, idx, 4));
                joueurs[idx - 1].score = stringToInt(getCell(fichierJoueurs, idx, 5));
                for (int idy = 6; idy < columnCount(fichierJoueurs); idy += 2) {
                    if (!equals(getCell(fichierJoueurs, idx, idy), "null")) {
                        nbChoix++;
                    }
                }
                matieres = new Matiere[nbChoix];
                for (int idy = 0; idy < length(matieres); idy++) {
                    matieres[idy] = new Matiere();
                    matieres[idy].nom = getCell(fichierJoueurs, idx, 6 + idy * 2);
                    matieres[idy].fini = equals(getCell(fichierJoueurs, idx, 7 + idy * 2), "true");
                }   
                joueurs[idx - 1].matieresChoisie = matieres;
                nbChoix = 0;
            }
        }
        return joueurs;
    }

    String scoreBoard(Joueur[] joueurs) {
        String affichage = "\nListe des parties :\n";
        for (int idx = 0; idx < length(joueurs, 1); idx++) {
            affichage += (idx+1) + " - " + joueurs[idx].pseudo + " - " + joueurs[idx].score + " points - Matières choisies : ";
            for (int idy = 0; idy < length(joueurs[idx].matieresChoisie); idy++) {
                if (joueurs[idx].matieresChoisie[idy].fini) {
                    affichage += ANSI_RED + joueurs[idx].matieresChoisie[idy].nom + " ";
                } else {
                    affichage += ANSI_BLUE + joueurs[idx].matieresChoisie[idy].nom + " ";
                }
            }
        }
        return affichage;
    }

    int maxNbrMatiere(Joueur[] joueurs) {
        int max = 0;
        for (int idx = 0; idx < length(joueurs); idx++) {
            if (length(joueurs[idx].matieresChoisie) > max) {
                max = length(joueurs[idx].matieresChoisie);
            }
        }
        return max;
    }

    void sauvegarderJoueur(Joueur joueur, int numJoueur) {
        Joueur[] joueurs = listerJoueurs();
        if (numJoueur == length(joueurs)) {
            Joueur[] nouveauJoueurs = new Joueur[length(joueurs) + 1];
            for (int idx = 0; idx < length(joueurs); idx++) {
                nouveauJoueurs[idx] = joueurs[idx];
            }
            nouveauJoueurs[numJoueur] = joueur;
            joueurs = nouveauJoueurs;
        } else {
            joueurs[numJoueur] = joueur;
        }
        String[][] sauvegarde = new String[length(joueurs) + 1][6 + maxNbrMatiere(joueurs) * 2];
        sauvegarde[0][0] = "Pseudo";
        sauvegarde[0][1] = "PositionLigne";
        sauvegarde[0][2] = "PositionColonne";
        sauvegarde[0][3] = "SalleActuelle";
        sauvegarde[0][4] = "EtageActuel";
        sauvegarde[0][5] = "Score";
        for (int idx = 6; idx < length(sauvegarde, 2); idx += 2) {
            sauvegarde[0][idx] = "Matiere";
            sauvegarde[0][idx + 1] = "Fini";
        }
        for (int idx = 0; idx < length(joueurs); idx++) {
            sauvegarde[idx + 1][0] = joueurs[idx].pseudo;
            sauvegarde[idx + 1][1] = "" + joueurs[idx].positionLigne;
            sauvegarde[idx + 1][2] = "" + joueurs[idx].positionColonne;
            sauvegarde[idx + 1][3] = "" + joueurs[idx].salleActuelle;
            sauvegarde[idx + 1][4] = "" + joueurs[idx].etageActuel;
            sauvegarde[idx + 1][5] = "" + joueurs[idx].score;
            for (int idy = 0; idy < length(joueurs[idx].matieresChoisie); idy++) {
                sauvegarde[idx + 1][6 + idy * 2] = joueurs[idx].matieresChoisie[idy].nom;
                sauvegarde[idx + 1][7 + idy * 2] = "" + joueurs[idx].matieresChoisie[idy].fini;
            }
        }
        saveCSV(sauvegarde, CHEMIN_SAUVEGARDE + "joueurs.csv");
    }
//

//TEST

    void testNewJoueur() {
        Joueur joueur = newJoueur("test");
        assertEquals("test", joueur.pseudo);
        assertEquals(1, joueur.positionLigne);
        assertEquals(1, joueur.positionColonne);
        assertEquals(1, joueur.salleActuelle);
        assertEquals(0, joueur.etageActuel);
        assertEquals(0, joueur.score);
        assertEquals('E', joueur.direction);
    }

    void testToStringJoueur() {
        Joueur joueur = newJoueur("test");
        String affichage = toString(joueur);
        String affichageAttendu = ANSI_GREEN + "test" + ANSI_RESET + ANSI_BOLD + " (0 points) est actuellement en " + ANSI_RED + "1" + ANSI_RESET + ANSI_BOLD + ", " + ANSI_RED + "1" + ANSI_RESET + ANSI_BOLD + " dans la salle " + ANSI_RED + "1" + ANSI_RESET + ANSI_BOLD + " de l'étage " + ANSI_RED + "0" + ANSI_RESET + ANSI_BOLD + ".";
        assertEquals(affichageAttendu, affichage);
    }

    void testNewQuestion() {
        Question question = newQuestion(1, "Test", "Réponse");
        assertEquals(1, question.difficulte);
        assertEquals("Test", question.intitule);
        assertEquals("Réponse", question.reponse);
        assertFalse(question.fini);
    }

    void testToStringQuestion() {
        Question question = newQuestion(1, "Test", "Réponse");
        String affichage = toString(question);
        String affichageAttendu = "Question de difficulté 1 : Test (Réponse)";
        assertEquals(affichageAttendu, affichage);
    }

    void testCompterQuestions() {
        Question[] questions = new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")};
        int[] compteur = compterQuestions(questions);
        assertEquals(1, compteur[0]);
        assertEquals(1, compteur[1]);
        assertEquals(1, compteur[2]);
    }

    void testTrierQuestions() {
        Question[] questions = new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")};
        Question[][] questionsTrie = trierQuestions(questions);
        assertEquals(1, length(questionsTrie[0]));
        assertEquals(1, length(questionsTrie[1]));
        assertEquals(1, length(questionsTrie[2]));
        assertEquals(1, questionsTrie[0][0].difficulte);
        assertEquals(2, questionsTrie[1][0].difficulte);
        assertEquals(3, questionsTrie[2][0].difficulte);
    }

    void testDifficulteFinie() {
        Matiere matiere = newMatiere("test");
        matiere.questions = trierQuestions(new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")});
        assertFalse(difficulteFinie(matiere, 0));
        assertFalse(difficulteFinie(matiere, 1));
        assertFalse(difficulteFinie(matiere, 2));
    }

    void testChoisirQuestions() {
        Matiere matiere = newMatiere("test");
        matiere.questions = trierQuestions(new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")});
        do {
            Question question = choisirQuestion(matiere);
            assertTrue(question.difficulte == 1 || question.difficulte == 2 || question.difficulte == 3);
            assertTrue(equals(question.intitule, "Test1") || equals(question.intitule, "Test2") || equals(question.intitule, "Test3"));
            assertTrue(equals(question.reponse, "Réponse1") || equals(question.reponse, "Réponse2") || equals(question.reponse, "Réponse3"));
            question.fini = true;
        } while (!difficulteFinie(matiere, 0) || !difficulteFinie(matiere, 1) || !difficulteFinie(matiere, 2));
    } 

    void testNewMatiere() {
        Matiere matiere = newMatiere("test");
        matiere.questions = trierQuestions(new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")});
        assertEquals("test", matiere.nom);
        assertEquals(1, matiere.questions[0][0].difficulte);
        assertEquals(2, matiere.questions[1][0].difficulte);
        assertEquals(3, matiere.questions[2][0].difficulte);
        assertEquals("Test1", matiere.questions[0][0].intitule);
        assertEquals("Test2", matiere.questions[1][0].intitule);
        assertEquals("Test3", matiere.questions[2][0].intitule);
        assertEquals("Réponse1", matiere.questions[0][0].reponse);
        assertEquals("Réponse2", matiere.questions[1][0].reponse);
        assertEquals("Réponse3", matiere.questions[2][0].reponse);
        assertFalse(matiere.fini);
    }

    void testToStringMatiere() {
        Matiere matiere = newMatiere("test");
        matiere.questions = trierQuestions(new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")});
        String affichage = toString(matiere);
        String affichageAttendu = "Matière test :\n" + toString(matiere.questions[0][0]) + "\n" + toString(matiere.questions[1][0]) + "\n" + toString(matiere.questions[2][0]) + "\n";
        assertEquals(affichageAttendu, affichage);
    }

    void testChoisirMatiere() {
        Joueur joueur = newJoueur("test");
        Matiere[] matieres = new Matiere[]{newMatiere("test1"), newMatiere("test2"), newMatiere("test3")};
        matieres[0].questions = trierQuestions(new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")});
        matieres[1].questions = trierQuestions(new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")});
        matieres[2].questions = trierQuestions(new Question[]{newQuestion(1, "Test1", "Réponse1"), newQuestion(2, "Test2", "Réponse2"), newQuestion(3, "Test3", "Réponse3")});
        joueur.matieresChoisie = matieres;
        do {
            Matiere matiere = choisirMatiere(joueur);
            assertTrue(equals(matiere.nom, "test1") || equals(matiere.nom, "test2") || equals(matiere.nom, "test3"));
            do {
                Question question = choisirQuestion(matiere);
                assertTrue(question.difficulte == 1 || question.difficulte == 2 || question.difficulte == 3);
                assertTrue(equals(question.intitule, "Test1") || equals(question.intitule, "Test2") || equals(question.intitule, "Test3"));
                assertTrue(equals(question.reponse, "Réponse1") || equals(question.reponse, "Réponse2") || equals(question.reponse, "Réponse3"));
                question.fini = true;
            } while (!difficulteFinie(matiere, 0) || !difficulteFinie(matiere, 1) || !difficulteFinie(matiere, 2));
            matiere.fini = true;
        } while (!matieres[0].fini || !matieres[1].fini || !matieres[2].fini);
    }

    void testTypeBordure() {
        Entites[][] salle = new Entites[][]{
            {Entites.BORDURE, Entites.BORDURE, Entites.BORDURE},
            {Entites.BORDURE, Entites.VIDE, Entites.BORDURE},
            {Entites.BORDURE, Entites.BORDURE, Entites.BORDURE}};
        assertEquals('▛', typeBordure(salle, 0, 0));
        assertEquals('▜', typeBordure(salle, 0, 2));
        assertEquals('▙', typeBordure(salle, 2, 0));
        assertEquals('▟', typeBordure(salle, 2, 2));
        assertEquals('▌', typeBordure(salle, 1, 0));
        assertEquals('▐', typeBordure(salle, 1, 2));
        assertEquals('▄', typeBordure(salle, 2, 1));
        assertEquals('▀', typeBordure(salle, 0, 1));
    }

    void testTypePorte() {
        Entites[][] salle = new Entites[][]{
            {Entites.BORDURE, Entites.PORTE_FERMEE, Entites.BORDURE},
            {Entites.PORTE_FERMEE, Entites.VIDE, Entites.PORTE_FERMEE},
            {Entites.BORDURE, Entites.PORTE_FERMEE, Entites.BORDURE}};
        assertEquals('▌', typePorte(salle, 1, 0));
        assertEquals('▐', typePorte(salle, 1, 2));
        assertEquals('▄', typePorte(salle, 2, 1));
        assertEquals('▀', typePorte(salle, 0, 1));
    }

    void testIsDigit() {
        assertTrue(isDigit('0'));
        assertTrue(isDigit('1'));
        assertTrue(isDigit('2'));
        assertTrue(isDigit('3'));
        assertTrue(isDigit('4'));
        assertTrue(isDigit('5'));
        assertTrue(isDigit('6'));
        assertTrue(isDigit('7'));
        assertTrue(isDigit('8'));
        assertTrue(isDigit('9'));
        assertFalse(isDigit('a'));
        assertFalse(isDigit('A'));
    }
    
    void testDifficulteToString() {
        assertEquals("*", difficulteToString(1));
        assertEquals("**", difficulteToString(2));
        assertEquals("***", difficulteToString(3));
    }

    void testBooleanToString() {
        assertEquals("true", booleanToString(true));
        assertEquals("false", booleanToString(false));
    }


}