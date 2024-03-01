import React, { useMemo } from "react";
import {
    DigitDesign,
    DigitLayout,
    DigitText
} from "@cthit/react-digit-components";

const GithubLink = ({ gh }) => (
    <a href={"https://github.com/" + gh}>
        <DigitText.Text text={"@" + gh} />
    </a>
);

const About = () => {
    const pg = useMemo(() => {
        const pg = [
            { nick: "Portals", gh: "Portals" },
            { nick: "Gurr", gh: "Gurr1" }
        ];
        pg.sort(() => {
            return 0.5 - Math.random();
        });
        return pg;
    }, []);

    return (
        <DigitLayout.Center>
            <DigitDesign.Card size={{ width: "300px" }}>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle
                        text={"Made by " + pg[0].nick + " and " + pg[1].nick}
                    />
                </DigitDesign.CardHeader>
                <DigitDesign.CardHeaderImage
                    style={{ objectFit: "contain" }}
                    src={"/digit18.png"}
                />
                <DigitDesign.CardBody>
                    <DigitLayout.Column>
                        <DigitText.Title text={"Github links:"} />
                        <DigitLayout.Row justifyContent={"space-around"}>
                            <GithubLink gh={pg[0].gh} />
                            <GithubLink gh={pg[1].gh} />
                        </DigitLayout.Row>
                        <a
                            href={"https://github.com/cthit/gamma"}
                            style={{ marginTop: "8px", alignSelf: "center" }}
                        >
                            <DigitText.Text text={"cthit/gamma"} />
                        </a>
                    </DigitLayout.Column>
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

export default About;
