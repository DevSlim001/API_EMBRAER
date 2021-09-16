import './index.css';
import createUser from '../../js/Usuario/CreateUser';

import { Button, Form } from 'react-bootstrap'

import $ from 'jquery';

function Cadastro(){
    async function handleCadastro(e){
        e.preventDefault()
        let usuario = {};
        $.each($('#form-cadastro').serializeArray(), function(i, field) {
            usuario[field.name] = field.value;
        });
        console.log(usuario)
        await createUser(usuario).then((res)=>{
            console.log(res)
        })

    }
    return(
        <div id="form-div">
            <Form id="form-cadastro" onSubmit={handleCadastro}>
                <Form.Group className="mb-3" controlId="email">
                    <Form.Label>Email</Form.Label>
                    <Form.Control type="email" name="email" placeholder="email@email.com" required/>
                </Form.Group>
                <Form.Group className="mb-3" controlId="nome">
                    <Form.Label>Nome</Form.Label>
                    <Form.Control type="text" name="nome" placeholder="Fulano" required/>
                </Form.Group>
                <Form.Group className="mb-3" controlId="role">
                    <Form.Label>Função</Form.Label>
                    <Form.Select aria-label="" defaultValue="" name="role" required>
                        <option value="" disabled >Selecione a função do usuário</option>
                        <option value="USER">Usuário</option>
                        <option value="ADMIN">Administrador</option>
                    </Form.Select>
                </Form.Group>
                <Button variant="primary" type="submit">Cadastrar</Button>
            </Form>
        </div>
    );
}

export default Cadastro;