import React from 'react';
import { Form,Row, Col } from 'react-bootstrap'


function FormCreateCodelist(props) {

    return (
        <div>
        <hr size="5"/>
        <Form name="form-create-codelist" className="form-create-codelist" >
            <Row>
                <Col xs="auto">
                    <Form.Group className="mb-3" controlId="numSecao">
                        <Form.Control type="text" name="numSecao" placeholder="Nº Seção" />
                    </Form.Group>
                </Col>
                <Col xs="auto">
                    <Form.Group className="mb-3" controlId="numSubSecao">
                        <Form.Control type="text" name="numSubSecao" placeholder="Nº SubSeção" />
                    </Form.Group>
                </Col>
                <Col >
                    <Form.Group className="mb-3" controlId="numBloco">
                        <Form.Control type="text" name="numBloco" placeholder="Nº Bloco" />
                    </Form.Group>
                </Col>
            </Row>
            <Row>
                <Col xs="auto">
                        <Form.Group className="mb-3" controlId="nomeBloco">
                            <Form.Control type="text" name="nomeBloco" placeholder="Nome do bloco" />
                        </Form.Group>
                    </Col>
                    <Col xs="auto">
                        <Form.Group className="mb-3" controlId="codBlocoCodelist">
                            <Form.Control type="text" name="codBlocoCodelist" placeholder="Código do bloco" />
                        </Form.Group>
                    </Col>
                    <Col >
                        <Form.Group className="mb-3" controlId="tracos">
                            <Form.Control type="text" name="tracos" placeholder="Traços" />
                        </Form.Group>
                    </Col>
            </Row>
        </Form>
        </div>
    );
}

export default FormCreateCodelist