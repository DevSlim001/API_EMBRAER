import './index.css';
import aviaologo from './../../images/aviaologo.png'
import auth from '../../js/Usuario/Auth';


function Login(){

async function handleSubmit(e){
    e.preventDefault();
    let senha = document.getElementById("senha").value
    let email = document.getElementById("email").value
    await auth(email,senha).then((res)=>{
        console.log(res);
    })

}
    return(
        <div className = "Login">
            <div id ="panel-left">
                <img src={aviaologo} width='50px' alt="LogoAvião"/>
                <h4 className="text">  Slim Aircraft Manual Composer</h4>
                <hr/>
                <div className="mb-3">
                    <center>
                        <img id="foto-perf" src={aviaologo} width= '100px' alt="Logo"/><br/>
                        <h1 className="text">Slim</h1><br/>
                    </center>
                    
                </div>
                <div className="form-div">
                    <form className="row g-3" onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">Email</label>
                            <input type="email" className="form-control" id="email" placeholder="usuario@email.com" />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="senha" className="form-label">Senha</label>
                            <input type="password" className="form-control" id="senha" />
                        </div>
                        <div className="mb-3 form-check col-auto">
                            <input className="form-check-input" type="checkbox" value="" id="conectado" />
                            <label className="form-check-label" htmlFor="conectado">Continuar conectado?</label>
                        </div>
                        <button type="submit" className="btn btn-primary">Login</button>
                        <br />
                        <button type="button" className="btn btn-dark ">Esqueceu sua senha?</button>
                    </form>
                </div>

            </div>
        </div>
    );
}

export default Login;