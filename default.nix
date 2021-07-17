with import <nixpkgs> {};

stdenv.mkDerivation {

  name = "pipeline";

  buildInputs = with pkgs; [
    git
    gitAndTools.gitflow
    adoptopenjdk-bin
    gradle
    docker-compose
  ];

  shellHook = ''
    if [ ! -d "./jdk" ]; then ln -s ${adoptopenjdk-bin} ./jdk; fi
    export JAVA_HOME="${adoptopenjdk-bin}"
  '';
}

