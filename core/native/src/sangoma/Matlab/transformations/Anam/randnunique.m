% produces n randnom number (from a gaussian distributions) which a guaranteed
% to be unique

function x = randnunique(n)
maxiter = 1000;

x = [];

i = 0;
while 1
  m = n - length(x)
  if m == 0
    break;
  end

  x = unique([x; randn(m,1)]);

  i = i+1;

  if i > maxiter
    error('max itereration reached');
  end
end

x = x(randperm(n));

% Copyright (C) 2012 Alexander Barth <a.barth@ulg.ac.be>
%
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2 of the License, or
% (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with this program; If not, see <http://www.gnu.org/licenses/>.