% MUNDO BLOQUES program
% Written by Fernanod Pinedo


% Predicates
:-dynamic sobre/2.
:-dynamic tamano/2.
:-dynamic color/2.
:-dynamic prisma/2.

% Facts
bloque(a).
prisma(a,cubo).
color(a,azul).
tamano(a,grande).
bloque(b).
prisma(b,cubo).
color(b,rojo).
tamano(b,mediano).
bloque(c).
prisma(c,caja).
tamano(c,chica).
color(c,amarilla).
bloque(d).
prisma(d,cilindro).
color(d,amarillo).
tamano(d,grande).
sobre(a,piso).
sobre(b,a).
sobre(c,b).
sobre(d,piso).

% Rules
poner(X,Y):-
assert(sobre(X,Y)),
write(' poniendo '),write(X),write(' sobre '),write(Y).

sacar(X,Z):-
retract(sobre(X,Z)),
write(' quitando '),write(X),write(' de '),write(Z).

quitar(Y,X):-
sobre(Y,X),
not(sobre(_,Y)),
poner(Y,piso),
sacar(Y,X).

quitar(Y,X):-
sobre(Y,X),
sobre(Z,Y),
quitar(Z,Y),
quitar(Y,X).

colocar(Y,X):-
not(sobre(_,Y)),
not(sobre(_,X)),
sobre(Y,Z),
quitar(Y,Z),
poner(Y,X).

colocar(Y,X):-
not(sobre(_,X)),
sobre(Z,Y),
quitar(Z,Y),
colocar(Y,X).

colocar(Y,X):-
sobre(W,X),
sobre(Z,Y),
quitar(W,X),
quitar(Z,Y),
colocar(Y,X).

