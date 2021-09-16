import { React, useState } from 'react'
import { Container, Row, Col, Navbar, Nav } from 'react-bootstrap'

import Header from './../../components/Header';
import Cadastro from './../Cadastro';
import "./index.css"

import { FaMarker,FaHome } from "react-icons/fa";


function Home(){
    const [conteudo, setConteudo] = useState();
    const renderCadastro = () =>(setConteudo(<Cadastro />))

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
                                <Nav.Link href="#"><FaHome /> <span>Home</span></Nav.Link>
                                <Nav.Link href="#cadastro" onClick={renderCadastro}><FaMarker /> <span>Cadastro</span></Nav.Link>
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
