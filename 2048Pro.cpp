#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

struct Usuario
{
    int numero;
    char alias[5];
    char nombre[20];
    char apellido[20];
    int edad;
    int puntajeMax;
};

void leer_linea(char arr[], int tam);

Usuario registrar(int &cont){//Registrar usuario nuevo
    Usuario retUser;
    int i=cont;
    printf("Alias(Maximo 5 letras): ");//Falta ver que no se repita
    leer_linea(retUser.alias, 5);
    printf("Nombre: ");
    leer_linea(retUser.nombre, 20);
    printf("Apellido: ");
    leer_linea(retUser.apellido, 20);
    printf("Edad: ");
    scanf("%d", &retUser.edad);
    getchar();
    retUser.puntajeMax=0;

    retUser.numero=i+1;
    

    return retUser;
}

void lista(Usuario user[], int &cont){//Mostrar lista de todos los usuarios creados
    int i;

    for(i=0; i<cont; i++){
        printf("%d-%s\n",user[i].numero, user[i].alias);//falta imprimir puntaje
    }
}

void datos(Usuario arr[], int &cont){
    char aliasB[5];
    int i=0, comp=1;

    printf("Escriba el alias: ");
    leer_linea(aliasB, 5);
    while(i<cont and comp!=0){
        comp=strcmp(aliasB,arr[i].alias);
        i++;
    }
    printf("Alias: %s\nNombre: %s\nApellido: %s\n", arr[i-1].alias, arr[i-1].nombre, arr[i-1].apellido);
    printf("Edad: %d\nPuntaje Maximo: %d\n", arr[i-1].edad, arr[i-1].puntajeMax);
}

void juego(Usuario arr[],int &cont){
    srand(time(NULL));
    int matriz[4][4], pos, pos2, pos3,filaR, colR, i, random, numAnt, filaNumAnt, colNumAnt, iguales=1, salida=0, ceros=1;
    int matAnt[4][4], imp=1, MAY=0, posUsuario=0, comp=1;
    char letra, enter, alias2[5];

    printf("Escriba su alias: ");
    leer_linea(alias2,5);
    while(posUsuario<cont and comp!=0){
        comp=strcmp(alias2,arr[posUsuario].alias);
        posUsuario++;
    }
    if(comp!=0){
        printf("No se encontro el alias, intente de nuevo o registrese.\n");
    }
    else{
        printf("Bienvenido %s\n", arr[posUsuario-1].alias);
        //MATRIZ EN BLANCO
        for(pos=0; pos<4; pos++){
            for(pos2=0; pos2<4; pos2++){
                matriz[pos][pos2]=0;
            }
        }
        //POSICION DE 2s
        for(i=0; i<2; i++){
            filaR=rand()%4;
            colR=rand()%4;
            if(matriz[filaR][colR]==0){
                matriz[filaR][colR]=2;
            }
            else
                i--;
        }

        while(salida==0 && iguales>0){

            //IMPRESION DE MATRIZ Y COPIA
            for(pos=0; pos<4; pos++){
                for(pos2=0; pos2<4; pos2++){
                    matAnt[pos][pos2]=matriz[pos][pos2];
                    if(matriz[pos][pos2]==0)
                        printf("|    |");
                    else
                        printf("|%4d|", matriz[pos][pos2]);
                }
                printf("\n");
            }

            //LEER COMANDO
            printf("Ingrese un comando: ");
            letra=getchar();
            enter=getchar();
            if(enter=='\n'){
                switch (letra)
                {
                case 'S':
                    /* BAJAR */
                    for(pos2=3; pos2>=0; pos2--){
                        numAnt=1;
                        for(pos=3; pos>=0; pos--){
                            if(matriz[pos][pos2]!=0){
                                if(matriz[pos][pos2]==numAnt){//LA PRIMERA VEZ DE CADA COL NO ENTRA PORQUE  numAnt=1
                                    matriz[pos][pos2]*=2;
                                    matriz[filaNumAnt][colNumAnt]=matriz[pos][pos2];
                                    matriz[pos][pos2]=0;
                                    numAnt=1;
                                }
                                else{
                                    numAnt=matriz[pos][pos2];
                                    colNumAnt=pos2;
                                    filaNumAnt=pos;
                                }
                            }   
                        }
                    }
                    for(pos2=3; pos2>=0; pos2--){
                        for(pos=3; pos>=0; pos--){
                            if(matriz[pos][pos2]!=0){
                                for(pos3=3; pos3>pos; pos3--){
                                    if(matriz[pos3][pos2]==0){
                                        matriz[pos3][pos2]=matriz[pos][pos2];
                                        matriz[pos][pos2]=0;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 'D':
                    /* DERECHA */
                for(pos=3; pos>=0; pos--){
                    numAnt=1;
                    for(pos2=3; pos2>=0; pos2--){
                        if(matriz[pos][pos2]!=0){
                            if(matriz[pos][pos2]==numAnt){
                                matriz[pos][pos2]*=2;
                                matriz[filaNumAnt][colNumAnt]=matriz[pos][pos2];
                                matriz[pos][pos2]=0;
                                numAnt=1;
                            }
                            else{
                                numAnt=matriz[pos][pos2];
                                colNumAnt=pos2;
                                filaNumAnt=pos;
                            }
                        }   
                    }
                }
                for(pos=3; pos>=0; pos--){
                    for(pos2=3; pos2>=0; pos2--){
                        if(matriz[pos][pos2]!=0){
                            for(pos3=3; pos3>pos2; pos3--){
                                if(matriz[pos][pos3]==0){
                                    matriz[pos][pos3]=matriz[pos][pos2];
                                    matriz[pos][pos2]=0;
                                }
                            }
                        }
                    }
                }
                    break;
                case 'W':
                    /* SUBIR */
                for(pos2=0; pos2<4; pos2++){
                    numAnt=1;
                    for(pos=0; pos<4; pos++){
                        if(matriz[pos][pos2]!=0){
                            if(matriz[pos][pos2]==numAnt){
                                matriz[pos][pos2]*=2;
                                matriz[filaNumAnt][colNumAnt]=matriz[pos][pos2];
                                matriz[pos][pos2]=0;
                                numAnt=1;
                            }
                            else{
                                numAnt=matriz[pos][pos2];
                                colNumAnt=pos2;
                                filaNumAnt=pos;
                            }
                        }   
                    }
                }
                for(pos2=0; pos2<4; pos2++){
                    for(pos=0; pos<4; pos++){
                        if(matriz[pos][pos2]!=0){
                            for(pos3=0; pos3<pos; pos3++){
                                if(matriz[pos3][pos2]==0){
                                    matriz[pos3][pos2]=matriz[pos][pos2];
                                    matriz[pos][pos2]=0;
                                }
                            }
                        }
                    }
                }
                    break;
                case 'A':
                    /* IZQUIERDA */
                    for(pos=0; pos<4; pos++){
                        numAnt=1;
                        for(pos2=0; pos2<4; pos2++){
                            if(matriz[pos][pos2]!=0){
                                if(matriz[pos][pos2]==numAnt){
                                    matriz[pos][pos2]*=2;
                                    matriz[filaNumAnt][colNumAnt]=matriz[pos][pos2];
                                    matriz[pos][pos2]=0;
                                    numAnt=1;
                                }
                                else{
                                    numAnt=matriz[pos][pos2];
                                    colNumAnt=pos2;
                                    filaNumAnt=pos;
                                }
                            }   
                        }
                    }
                    for(pos=0; pos<4; pos++){
                        for(pos2=0; pos2<4; pos2++){
                            if(matriz[pos][pos2]!=0){
                                for(pos3=0; pos3<pos2; pos3++){
                                    if(matriz[pos][pos3]==0){
                                        matriz[pos][pos3]=matriz[pos][pos2];
                                        matriz[pos][pos2]=0;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 'E':
                    salida=1;
                    for(pos=0; pos<4; pos++)
                        for(pos2=0; pos2<4; pos2++)
                            if(matriz[pos][pos2]>MAY)
                                MAY=matriz[pos][pos2];
                    printf("Hiciste %d puntos.\n", MAY);
                    arr[posUsuario-1].puntajeMax+=MAY;
                    break;
                default:
                    printf("Comando incorrecto\n");
                    break;
                }
            }
            else{
                printf("Comando incorrecto\n");
                while(enter!='\n')
                    enter=getchar();
            }
        
            //COMPARAR MATRIZ ANTERIOR CON ACTUAL, SI SON IGUALES NO GENERAR NUEVO NUMERO
            imp=0;
            for(pos=0; pos<4; pos++){
                for(pos2=0; pos2<4; pos2++){
                    if(matriz[pos][pos2]!=matAnt[pos][pos2]){
                        imp=1;
                    }
                }
            }

            if(imp==1){    
                //POSICION DE 2 y 4
                if(letra!='E' && ceros!=0){
                    for(i=0; i<1; i++){
                        filaR=rand()%4;
                        colR=rand()%4;
                        if(matriz[filaR][colR]==0){
                            random=rand();
                            if(random%2==0)
                                matriz[filaR][colR]=2;
                            else    
                                matriz[filaR][colR]=4;
                        }
                        else
                            i--;
                    }
                }
            
                //VER SI GANO
                for(pos=0; pos<4; pos++)
                    for(pos2=0; pos2<4; pos2++)
                        if(matriz[pos][pos2]==2048){
                            printf("QUE RICA MANITO, GANASTE! HICISTE 2048 PUNTOS.\n");
                            arr[posUsuario-1].puntajeMax+=2048;
                            for(pos=0; pos<4; pos++){
                                for(pos2=0; pos2<4; pos2++){
                                    if(matriz[pos][pos2]==0)
                                        printf("|    |");
                                    else
                                        printf("|%4d|", matriz[pos][pos2]);
                                }
                            printf("\n");
                            }
                            salida=1;
                        }

                //VER SI HAY CEROS(ESPACIOS)
                ceros=0;
                for(pos=0; pos<4; pos++)
                    for(pos2=0; pos2<4; pos2++)
                        if(matriz[pos][pos2]==0){
                            pos2=4;
                            pos=4;
                            ceros++;
                        }

                //SI NO HAY ESPACIOS(CEROS) VE SI HAY MOVIMIENTO DISPONIBLE
                if(ceros==0){
                    iguales=0;
                    for(pos=0; pos<4; pos++){
                        for(pos2=0; pos2<4; pos2++){
                            if(matriz[pos][pos2]>MAY)
                                MAY=matriz[pos][pos2];
                            if(pos<3)
                                if(matriz[pos][pos2]==matriz[pos+1][pos2])//COMPARA CON ABAJO
                                    iguales++;
                            if(pos2<3)
                                if(matriz[pos][pos2]==matriz[pos][pos2+1])//COMPARA CON DERECHA
                                    iguales++;
                        }
                    }
                    if(iguales==0){
                        printf("PERDISTE! HICISTE %d PUNTOS! SOS ESPANTOSO!\n", MAY);
                        arr[posUsuario-1].puntajeMax+=MAY;
                        for(pos=0; pos<4; pos++){
                            for(pos2=0; pos2<4; pos2++){
                                if(matriz[pos][pos2]==0)
                                        printf("|    |");
                                    else
                                        printf("|%4d|", matriz[pos][pos2]);
                            }
                            printf("\n");
                        }
                    }
                }
            }
        }
    }
}

bool compararUsuarios(int Usuario[], int UsuarioN[], int tamaño) {
    for (int i = 0; i < tamaño; i++) {
        if (Usuario[i] != UsuarioN[i]) {
            return false;
        }
    }
    return true;
}
int main(){
    int contU=0, salida=1, i;
    char opcion, enter;
    Usuario usuario[10];

    printf("**Bienvenido a 2048**\n");

    while(salida==1){
        printf("(R)egistrar (L)istado (J)ugar (D)atos (E)liminar (S)alir\nOpcion: ");
        opcion=getchar();
        enter=getchar();
        if(enter!='\n'){
            while(enter!='\n'){
                enter=getchar();
            }
            printf("Opcion incorrecta.");
        }
        else{
            switch (opcion)
            {
            case 'R':
                printf("Registrar\n");
                usuario[contU]=registrar(contU);
                contU++;
                break;
            case 'L'://Imprimir lista de jugadores
                printf("Lista\n");
                lista(usuario, contU);
                break;
            case 'J'://Jugar
                printf("Jugar\n");
                juego(usuario,contU);
                
                break;
            case 'D'://Datos de un jugador
                printf("Datos\n");
                datos(usuario, contU);
                break;
            case 'E'://Eliminar
                printf("Eliminar\n");

                break;
            case 'S'://Salida
                salida=0;
                break;
            }
        }
    }
    
}

void leer_linea(char arr[], int tam){
    int pos=0;
    char letra;

    letra=getchar();
    while(letra!='\n' and pos<tam-1){
        arr[pos]=letra;
        pos++;
        letra=getchar();
    }
    arr[pos]='\0';
    while(letra!='\n')
        letra=getchar();
}
