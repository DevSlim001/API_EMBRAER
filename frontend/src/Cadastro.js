import './index.css';
import aviaologo from './aviaologo.png'

function Cadastro(){
    return(
        <div className="Cadastro">
            <div className="header">
                <img src={aviaologo} width='50px'/>
                <h4 className="text" id="title-cadastro">Slim Aircraft Manual Composer</h4>
            </div>
            <b><h4 id="cadastrarUsuario">Cadastrar usuário</h4></b>
            <div className="cadastro-inputs">
                <h6 className="inline">Nome:</h6>
                <input id="textbox" type="text"/><br/><br/>

                <h6 className="inline">CPF:</h6>
                <input id="textbox" type="text"/><br/><br/>

                <h6 className="inline">Telefone:</h6>
                <input id="textbox" type="tel"/><br/><br/>

                <h6 className="inline">Matricula:</h6>
                <input id="textbox" type="text"/><br/><br/>

                <h6 className="inline">e-mail:</h6>
                <input id="textbox" type="email"/><br/><br/>

                <h6 className="inline">senha:</h6>
                <input id="textbox" type="password"/><br/><br/>

                <h6 className="inline">Confirmar senha:</h6>
                <input id="textbox" type="password"/><br/><br/>

                <button className="salvar" onClick="">Salvar</button>
            </div>
            
        </div>
    );
}

export default Cadastro;