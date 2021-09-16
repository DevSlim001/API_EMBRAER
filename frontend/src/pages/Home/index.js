import { React, useState } from 'react'
import { Container, Row, Col, Navbar, Nav, Button, Alert, Spinner } from 'react-bootstrap'

import Header from './../../components/Header';
import Cadastro from './../Cadastro';
import "./index.css"


function Home(){
    const [conteudo, setConteudo] = useState();
    function renderCadastro(){
        setConteudo(<Cadastro />);
    }
    return (
        <Container fluid>
            <Row>
                <Header id="header" />
            </Row>
            <Row>
                <Col xs={2} id="navbar">
                    <Navbar bg="dark" variant="dark">
                        <Container>
                            <Nav fill className="flex-column">
                                <Nav.Link href="#home">Home</Nav.Link>
                                <Nav.Link onClick={renderCadastro}>Cadastro</Nav.Link>
                                <Nav.Link href="#pricing">Pricing</Nav.Link>
                            </Nav>
                        </Container>
                    </Navbar>
                </Col>
                <Col id="body">
                    <div id="div-body">
                        {conteudo}
                    </div>
                </Col>
            </Row>
        </Container>
    )
}

export default Home;
