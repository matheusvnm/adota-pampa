import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Animal.class)
public class Animal {

    protected int id;
    protected String name;
    protected String genero;
    protected String idade;
    protected String image;
    protected boolean isAdotado;
    protected String nomeDono;
    protected String telefoneDono;

    @JsonCreator
    public Animal(){}

    public Animal(int id, String name, String genero, String idade, String image, boolean isAdotado, String nomeDono, String telefoneDono) {
        this.id = id;
        this.name = name;
        this.genero = genero;
        this.idade = idade;
        this.image = image;
        this.isAdotado = isAdotado;
        this.nomeDono = nomeDono;
        this.telefoneDono = telefoneDono;
    }

    public String toStringSimple() {
        return "Nome: " + name +". Idade: " + idade;
    }

    public String toStringDetalhado() {
        return
                "Name: " + name +
                ". Genero: " + genero +
                ". Idade: " + idade +
                ". Image: " + image;
    }

    public String toStringSoContato(){
        return "Nome do Dono: " + nomeDono +
                ". Telefone do Dono: " + telefoneDono;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genero='" + genero + '\'' +
                ", idade='" + idade + '\'' +
                ", image='" + image + '\'' +
                ", isAdotado=" + isAdotado +
                ", nomeDono='" + nomeDono + '\'' +
                ", telefoneDono='" + telefoneDono + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsAdotado() {
        return isAdotado;
    }

    public void setAdotado(boolean adotado) {
        isAdotado = adotado;
    }

    public String getNomeDono() {
        return nomeDono;
    }

    public void setNomeDono(String nomeDono) {
        this.nomeDono = nomeDono;
    }

    public String getTelefoneDono() {
        return telefoneDono;
    }

    public void setTelefoneDono(String telefoneDono) {
        this.telefoneDono = telefoneDono;
    }
}
