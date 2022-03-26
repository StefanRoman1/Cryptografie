#include <iostream>
#include <string>
#include <fstream>

using namespace std;

string initial_text;
string initial_key = "iniiniiniint";
string crypto_text;
string discovered_key;
int discovered_key_size;
double frecvente_litere[26] = { 0.082, 0.015, 0.027, 0.043, 0.13, 0.022,
                                0.02, 0.062, 0.069, 0.0015, 0.0078, 0.041,
                                0.025, 0.067, 0.078, 0.019, 0.00096, 0.059,
                                0.062, 0.096, 0.027, 0.0097, 0.024, 0.0015,
                                0.02, 0.00078 };

void text_formating()/*eliminam spatiile, semnele de punctuatie si caractere speciale din plain text*/
{
    for (int i = 0; i < initial_text.length(); i++)
    {
        if (initial_text[i] < 'A' || (initial_text[i] > 'Z' && initial_text[i] < 'a') || initial_text[i]>'z')
        {
            initial_text.erase(i, 1);
            i--;
        }
    }
}

void to_upper_case()/*transformam toate litere mici din cheie si plain text in litere mari*/
{
    for (int i = 0; i < initial_text.length(); i++)
    {
        if (initial_text[i] >= 'a' && initial_text[i] <= 'z')
        {
            initial_text[i] = initial_text[i] - 32;
        }
        if (initial_key[i] >= 'a' && initial_key[i] <= 'z')
        {
            initial_key[i] = initial_key[i] - 32;
        }
    }
}

void equal_initial_key_length()/*egalam lungimea cheii cu plain textul prin concatenarea ei la final*/
{
    int index_cheie = 0;
    if (initial_key.length() < initial_text.length())
    {
        while (initial_key.length() < initial_text.length())
        {
            initial_key += initial_key[index_cheie];
            index_cheie++;
            if (index_cheie == initial_key.length())
            {
                index_cheie = 0;
            }
        }
    }
}

void encrypting()/*criptam plain textul*/
{
    for (int i = 0; i < initial_text.length(); i++)
    {
        char x = (initial_text[i] + initial_key[i]) % 26;
        x += 'A';
        crypto_text += x;
    }
}

void imput()/*citim de la tastatura numele fisierului in care se afla plain textul si il salvam in <initial_text>*/
{
    ifstream fin;
    char filename[50];
    cout << "Enter name of the file : ";
    cin >> filename;
    fin.open(filename);
    while (fin.good() == false)
    {
        cout << "File not found. Try again" << endl;
        cout << "Enter name of the file : ";
        cin >> filename;
        fin.open(filename);
    }
    while (fin.eof() == false)
    {
        initial_text += fin.get();
    }
    fin.close();
}

int frecventa(string text, char c)/*returneaza numarul de aparitii al caracterului <c> in textul <text>*/
{
    int frec = 0;
    for (int i = 0; i < text.length(); i++)
    {
        if (text[i] == c)
        {
            frec++;
        }
    }
    return frec;
}

double IC(string sir)/*returneaza indicele de coincidenta al textului <sir>*/
{
    double result = 0;
    double size = sir.length();
    for (char c = 'A'; c <= 'Z'; c++)
    {
        double a = (double)(frecventa(sir, c)/size);
        double b = (double)((frecventa(sir, c) - 1) / (size - 1));
        result += (a * b);
    }
    return result;
}

int key_length()/*functia care returneaza lungimea cheii*/
{
    double sum_result = 0;
    string subsir;
    int size = crypto_text.length();
    for (int m = 2; m < size/2; m++)/*incercam toate lungimile cuprinse intre 2 si jumatatea numarului,
                                    returnam prima valoare a ei, deoarece restul for fi multipli de lungimea cheii*/
    {
        sum_result = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < size; j++)
            {
                if (j % m == i)
                {
                    subsir += crypto_text[j]; /*creem un subsir din mi in mi pozitii, unde m este lungimea cheii pe care o verificam*/
                }
            }
            sum_result += IC(subsir);/*facem suma tuturor indicelor de frecventa ale subsirurilor*/
            subsir.clear();
        }
        if (sum_result / m >= 0.064 && sum_result / m <= 0.067) /*daca rezultatul este aproximativ 0.065 ne oprim deoarce am gasit lungimea cheii*/
        {
            return m;
        }
    }
    return -1;
}

void filtrare_fisier_initial() /* primii pasi necesari criptarii si decriptarii textului*/
{
    text_formating();
    equal_initial_key_length();
    to_upper_case();
}

string shiftare(string text,int poz)/* shiftam textul <text> cu <poz> pozitii mai la stanga*/
{
    for (int i = 0; i < text.length(); i++) {
        text[i] = 'A'+ (text[i] - poz + 'A') % 26;
            
    }
    return text;
}

double MIC(string text) /*retureneaza indicele mutual de coincidenta dintre textul primit ca parametru si un text normal in limba engleza*/
{
    double indice = 0;
    int i = -1;
    double lungime_text = text.length();
    for (char c = 'A'; c <= 'Z'; c++)
    {
        i++;
        double a = frecvente_litere[i];
        double b = (double)(frecventa(text, c) / lungime_text);
        indice += (a * b);
    }
    return indice;
}

void elemente_cheie()/*gasirea efectiva a elementelor cheii*/
{
    double indice_MIC;
    for (int i = 0; i < discovered_key_size; i++)
    {
        string subsir,copie_subsir;
        for (int j = 0; j < crypto_text.length(); j++)
        {
            if (j % discovered_key_size == i)
            {
                subsir += crypto_text[j];/*creem un subsir cu elementele din mi in mi din textul criptat un m este lungimea cheii pe care am descoperit-o*/
            }
        }
        copie_subsir = subsir;
        int s = 0;
        while(s<=25)
        {
            
            indice_MIC = MIC(subsir); /*calculam indicele mic pentru fiecare subsir, daca nu este aproximativ 0.065, shiftam textul la stanga cu o pozitie si recalculam*/
            if (indice_MIC >= 0.05 && indice_MIC <= 0.07)
            {
                break;
            }
            s++;
            subsir.clear();
            subsir = shiftare(copie_subsir, s);
        }
        char k = (s) % 26;
        k += 'A';
        discovered_key += k; /*cand ajungem la shiftarea corecta, punem litera corespunzatoare numarului de shiftari efectuate*/
        subsir.clear();
    }
}

string decrypting(string crypto_text, string cheie)/*decriptam crypto_textul folosing cheia descoperita*/
{
    string output;
    int j = 0;
    for (int i = 0; i < crypto_text.length(); i++)
    {
        char c = (crypto_text[i] - cheie[j] + 26) % 26;
        c += 'A';
        output += c;
        j++;
        if (j > cheie.length() - 1)
            j = 0;
    }
    return output;
}

int main()
{
    imput();//citim plain textul
    filtrare_fisier_initial();//filtram plain textul si cheia
    cout << initial_text.length() << endl << endl;
    encrypting();//criptam textul
    discovered_key_size = key_length();//gasim lungimea cheii
    elemente_cheie();//gasim elementele din cheie
    cout << "Discovered key : " << discovered_key << endl;
    cout << "Decritpted text : " << decrypting(crypto_text, discovered_key);  //decriptam textul  

}
